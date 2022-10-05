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
import org.springframework.transaction.annotation.Transactional

@Service
class TrackingQrTransaction(
    private val qrCodeService: QrCodeService,
    private val bakongOpenAPIClientHelper: BakongOpenAPIClientHelper
) {
    private val LOG = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "2 * * * * *")
    @Transactional
    fun trackingQrTransactionStatus() {
        val pendingQrCodes = qrCodeService.getAllPendingQrCode()

        if (pendingQrCodes.isNotEmpty()) {
            LOG.info(">>> Tracking Transaction By QR Id: ${pendingQrCodes.map { it.id }}")

            val trackingSuccessQrIds = mutableSetOf<Long>()
            val trackingFailQrIds = mutableSetOf<Long>()
            val retryQrCode = mutableSetOf<Long>()

            pendingQrCodes.forEach { qrCode ->
                this.trackingBakongTransactionByMD5(qrCode).let {
                    when (it) {
                        QrCodeStatusEnum.SUCCESS.id -> trackingSuccessQrIds.add(qrCode.id)
                        QrCodeStatusEnum.FAILED.id -> trackingFailQrIds.add(qrCode.id)
                        else -> {
                            retryQrCode.add(qrCode.id)
                            LOG.info(">>> The QR Code id is in tracking: ${qrCode.id}")
                        }
                    }
                }
            }
            qrCodeService.saveToSuccessQrCodes(trackingSuccessQrIds)
            qrCodeService.saveWithIncreaseRetryAttempt(retryQrCode)
            qrCodeService.saveToFailQrCodes(trackingFailQrIds)
        }
    }

    private fun trackingBakongTransactionByMD5(qrCodeProjection: QrCodeProjection): Long {
        val res = bakongOpenAPIClientHelper.checkTransactionWithMd5(qrCodeProjection.md5)
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