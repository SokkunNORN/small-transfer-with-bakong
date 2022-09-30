package me.transfer.transferbakongapi.service.impl

import kh.org.nbc.bakong_khqr.BakongKHQR
import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.api.response.QrRes
import me.transfer.transferbakongapi.api.response.helper.err
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.repository.CurrencyTypeRepository
import me.transfer.transferbakongapi.service.IKHQRTransactionService
import org.springframework.stereotype.Service
import java.util.*

@Service
class KHQRTransactionService(
    private val qrCodeService: QrCodeService,
    private val transactionService: TransactionService,
    private val currencyRepository: CurrencyTypeRepository
) : IKHQRTransactionService {
    override fun generateQr(request: QrReq): QrRes {
        val currency = getOrElseThrow("Currency", request.currencyId!!, currencyRepository::findById)
        val merchantInfo = qrCodeService.getMerchantInformation(request)

        val khQrRes = BakongKHQR.generateMerchant(merchantInfo)
        if (khQrRes.khqrStatus.code != 0) err("Error generating KH QR")
        val md5 = khQrRes.data.md5
        val qrString = khQrRes.data.qr

        qrCodeService.saveQRCode(
            qrString,
            md5,
            currency,
            merchantInfo.amount.toBigDecimal(),
            merchantInfo.billNumber,
            description = "Dynamic QR Code",
            merchantInfo.storeLabel,
            merchantInfo.terminalLabel
        )

        return QrRes(
            request.amount!!,
            qrString,
            md5,
            currency,
            merchantInfo.terminalLabel
        )
    }

    private fun getRandomNumberString(): String {
        val rnd = Random()
        val number: Int = rnd.nextInt(999999)
        return String.format("%06d", number)
    }
}