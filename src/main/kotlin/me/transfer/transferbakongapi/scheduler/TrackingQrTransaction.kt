package me.transfer.transferbakongapi.scheduler

import me.transfer.transferbakongapi.api.bakong_client.enum.ErrorCode
import me.transfer.transferbakongapi.api.bakong_client.enum.ResponseCode
import me.transfer.transferbakongapi.api.bakong_client.open.helper.BakongOpenAPIClientHelper
import me.transfer.transferbakongapi.command.enum.QrCodeStatusEnum
import me.transfer.transferbakongapi.demain.projection.QrCodeProjection
import me.transfer.transferbakongapi.service.impl.QrCodeService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.api.bakong_client.dto.ResponseWrapper
import me.transfer.transferbakongapi.api.request.QrCodeBakongTransactionReq

@Service
class TrackingQrTransaction(
    private val qrCodeService: QrCodeService,
    private val bakongOpenAPIClientHelper: BakongOpenAPIClientHelper
) {
    private val LOG = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "2 * * * * *")
    fun trackingQrTransactionStatus() {
        val pendingQrCodes = qrCodeService.getAllPendingQrCode()

        if (pendingQrCodes.isNotEmpty()) {
            LOG.info(">>> Tracking Transaction By QR Id: ${pendingQrCodes.map { it.id }}")

            val trackingFailQrIds = mutableSetOf<Long>()
            val retryQrCode = mutableSetOf<Long>()
            val trackingSuccessQrCodes = mutableSetOf<QrCodeBakongTransactionReq>()

            pendingQrCodes.forEach { qrCode ->
                val bakongResponse = bakongOpenAPIClientHelper.checkTransactionWithMd5(qrCode.md5)

                this.trackingBakongTransactionByMD5(bakongResponse).let {
                    when (it) {
                        QrCodeStatusEnum.SUCCESS.id -> {
                            trackingSuccessQrCodes.add(
                                QrCodeBakongTransactionReq(
                                    qrCode = qrCode,
                                    bakongTrx = bakongResponse.data!!
                                )
                            )
                        }
                        QrCodeStatusEnum.FAILED.id -> trackingFailQrIds.add(qrCode.id)
                        else -> {
                            retryQrCode.add(qrCode.id)
                            LOG.info(">>> The QR Code id is in tracking: ${qrCode.id}")
                        }
                    }
                }
            }
            qrCodeService.saveToSuccessQrCodes(trackingSuccessQrCodes)
            qrCodeService.saveWithIncreaseRetryAttempt(retryQrCode)
            qrCodeService.saveToFailQrCodes(trackingFailQrIds)
        }
    }

    private fun trackingBakongTransactionByMD5(res: ResponseWrapper<CheckTransactionMd5Dto.Response>): Long {
        val data = res.data

        when (res.responseCode) {
            ResponseCode.SUCCESS.code -> {
                if (data != null) {
                    LOG.info(">>> Bakong Transaction is created successfully with hast ${data.hash}")
                    return QrCodeStatusEnum.SUCCESS.id
                } else {
                    LOG.info(">>> Bakong Response Code Success but no transaction found")
                }
            }
            ResponseCode.FAIL.code -> {
                when (res.errorCode) {
                    ErrorCode.TRANSACTION_NOT_FOUND.code -> LOG.info(">>> Bakong Transaction is not found")
                    ErrorCode.TRANSACTION_FAILED.code -> {
                        LOG.info(">>> Bakong Transaction is failed")
                        return QrCodeStatusEnum.FAILED.id
                    }
                    else -> LOG.info(">>> Bakong Transaction is unknown error")
                }
            }
        }

        return -1L
    }
}