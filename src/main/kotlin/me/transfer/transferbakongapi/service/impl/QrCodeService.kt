package me.transfer.transferbakongapi.service.impl

import kh.org.nbc.bakong_khqr.model.KHQRCurrency
import kh.org.nbc.bakong_khqr.model.MerchantInfo
import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.command.enum.CurrencyEnum
import me.transfer.transferbakongapi.command.enum.QrCodeStatusEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.model.CurrencyType
import me.transfer.transferbakongapi.model.QrCode
import me.transfer.transferbakongapi.repository.QrCodeRepository
import me.transfer.transferbakongapi.repository.QrCodeStatusRepository
import me.transfer.transferbakongapi.service.IQrCodeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class QrCodeService(
    private val qrCodeRepo: QrCodeRepository,
    private val qrCodeStatusRepo: QrCodeStatusRepository
) : IQrCodeService {
    private val LOG = LoggerFactory.getLogger(javaClass)
    fun getMerchantInformation(request: QrReq): MerchantInfo {
        val merchantInfo = MerchantInfo()
        val billNumber = getRandomNumberString()

        merchantInfo.bakongAccountId = "devbkhppxxx@dev"
        merchantInfo.merchantId = "00000001"
        merchantInfo.acquiringBank = "Dev Bank"
        merchantInfo.currency = getKhQrCurrency(request.currencyId!!)
        merchantInfo.amount = request.amount
        merchantInfo.merchantName = "John Smith"
        merchantInfo.merchantCity = "PHNOM PENH"
        merchantInfo.billNumber = "#$billNumber#".take(25)
        merchantInfo.mobileNumber = "".take(12)
        merchantInfo.storeLabel = "Coffee Shop"
        merchantInfo.terminalLabel = "Cashier_1"

        return merchantInfo
    }

    fun saveQRCode(
        qrString: String,
        md5: String,
        currency: CurrencyType,
        amount: BigDecimal,
        billNumber: String,
        description: String?,
        cushierLabel: String,
        terminalLabel: String
    ) : QrCode {
        val qrCodeStatus = getOrElseThrow("QrCodeStatus", QrCodeStatusEnum.PENDING.id, qrCodeStatusRepo::findById)
        val qrCode = QrCode(
            qrString = qrString,
            md5 = md5,
            amount = amount,
            billNumber = billNumber,
            description = description,
            cushierLabel = cushierLabel,
            terminalLabel = terminalLabel,
            retryAttempted = 0
        ).apply {
            this.currency = currency
            this.status = qrCodeStatus
        }

        return qrCodeRepo.save(qrCode)
    }

    private fun getKhQrCurrency(currencyId: Long): KHQRCurrency {
        return if (currencyId == CurrencyEnum.KHR.id) {
            KHQRCurrency.KHR
        } else {
            KHQRCurrency.USD
        }
    }

    private fun getRandomNumberString(): String {
        val rnd = Random()
        val number: Int = rnd.nextInt(999999)
        return String.format("%06d", number)
    }
}