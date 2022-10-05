package me.transfer.transferbakongapi.service.impl

import kh.org.nbc.bakong_khqr.BakongKHQR
import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.api.response.QrRes
import me.transfer.transferbakongapi.api.response.helper.err
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.demain.model.QrCode
import me.transfer.transferbakongapi.repository.CurrencyTypeRepository
import me.transfer.transferbakongapi.repository.QrCodeRepository
import me.transfer.transferbakongapi.service.IKHQRTransactionService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class KHQRTransactionService(
    private val qrCodeService: QrCodeService,
    private val currencyRepository: CurrencyTypeRepository
) : IKHQRTransactionService {
    private val LOG = LoggerFactory.getLogger(javaClass)
    override fun generateQr(request: QrReq): QrRes {
        val currency = getOrElseThrow("Currency", request.currencyId!!, currencyRepository::findById)
        val merchantInfo = qrCodeService.getMerchantInformation(request)

        val khQrRes = BakongKHQR.generateMerchant(merchantInfo)
        if (khQrRes.khqrStatus.code != 0) err("Error generating KH QR")
        val md5 = khQrRes.data.md5
        val qrString = khQrRes.data.qr

        val qrCode = qrCodeService.saveQRCode(
            qrString,
            md5,
            currency,
            merchantInfo.amount.toBigDecimal(),
            merchantInfo.billNumber,
            description = "Dynamic QR Code",
            merchantInfo.storeLabel,
            merchantInfo.terminalLabel
        )
        LOG.info(">>> Created QR ${qrCode.billNumber}")

        return QrRes(
            request.amount!!,
            qrString,
            md5,
            currency,
            merchantInfo.terminalLabel
        )
    }
}