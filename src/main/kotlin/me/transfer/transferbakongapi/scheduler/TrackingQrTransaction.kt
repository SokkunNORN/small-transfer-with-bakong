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
import org.springframework.scheduling.annotation.Async
import org.springframework.util.StopWatch
import java.util.concurrent.CompletableFuture

@Service
class TrackingQrTransaction(
    private val qrCodeService: QrCodeService,
    private val bakongOpenAPIClientHelper: BakongOpenAPIClientHelper
) {
    private val LOG = LoggerFactory.getLogger(javaClass)

    /**
     * @Scheduled
     * [fixedRate]: No delay and start from one executed to start new execute in fixedRate value ms.
     * [fixedDelay]: Delay from end executed to start new execute in fixedDelay value ms.
     * [initialDelay]: Initial delay when start to executing in initialDelay value ms
     * [@cron]: recommend on execute fun start from hour, date, month, year
     * **/
    @Scheduled(initialDelay = 2000, fixedRate = 2000)
    @Async
    fun trackingQrTransactionStatus() {
        val pendingQrCodes = qrCodeService.getAllPendingQrCode()

        if (pendingQrCodes.isNotEmpty()) {
            val trackingFailQrIds = mutableSetOf<Long>()
            val retryQrCode = mutableSetOf<Long>()
            val trackingSuccessQrCodes = mutableSetOf<QrCodeBakongTransactionReq>()

            CompletableFuture.supplyAsync {
                val stopwatch = StopWatch()
                stopwatch.start()

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
                            else -> retryQrCode.add(qrCode.id)
                        }
                    }
                }
                stopwatch.stop()
                LOG.info("### Tracking QR Code time: ${stopwatch.totalTimeMillis} ms") // Timing: 17023 ms with 105 transactions

                qrCodeService.saveToSuccessQrCodes(trackingSuccessQrCodes)
                qrCodeService.saveWithIncreaseRetryAttempt(retryQrCode)
                qrCodeService.saveToFailQrCodes(trackingFailQrIds)
            }
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