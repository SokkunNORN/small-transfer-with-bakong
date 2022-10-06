package me.transfer.transferbakongapi.service.impl

import kh.org.nbc.bakong_khqr.model.KHQRCurrency
import kh.org.nbc.bakong_khqr.model.MerchantInfo
import me.transfer.transferbakongapi.api.request.QrCodeBakongTransactionReq
import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.command.Constants
import me.transfer.transferbakongapi.command.enum.CurrencyEnum
import me.transfer.transferbakongapi.command.enum.QrCodeStatusEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.demain.model.CurrencyType
import me.transfer.transferbakongapi.demain.model.QrCode
import me.transfer.transferbakongapi.repository.QrCodeRepository
import me.transfer.transferbakongapi.repository.QrCodeStatusRepository
import me.transfer.transferbakongapi.service.IQrCodeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class QrCodeService(
    private val qrCodeRepo: QrCodeRepository,
    private val qrCodeStatusRepo: QrCodeStatusRepository,
    private val transactionService: TransactionService
) : IQrCodeService {
    private val LOG = LoggerFactory.getLogger(javaClass)
    fun getMerchantInformation(request: QrReq): MerchantInfo {
        val merchantInfo = MerchantInfo()
        val billNumber = getRandomNumberString()

        merchantInfo.bakongAccountId = Constants.ACQUIRER_BAKONG_ACCOUNT_ID
        merchantInfo.merchantId = Constants.ACQUIRER_BAKONG_BANK_ID
        merchantInfo.acquiringBank = Constants.ACQUIRER_BAKONG_BANK_NAME
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

    fun initQrCode(
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
            terminalLabel = terminalLabel
        ).apply {
            this.currency = currency
            this.status = qrCodeStatus
        }

        return qrCodeRepo.save(qrCode)
    }

    fun saveToSuccessQrCodes(requests: Set<QrCodeBakongTransactionReq>) {
        if (requests.isNotEmpty()) {
            LOG.info("Successful transaction QR Code id: ${requests.map { it.qrCode.id }}")
            val status = getOrElseThrow("QR Code Status", QrCodeStatusEnum.SUCCESS.id, qrCodeStatusRepo::findById)
            val qrCodeIds = requests.map { it.qrCode.id }.toSet()
            val successQrCodes = this.getAllQrCodeByIds(qrCodeIds).map {
                it.status = status
                it
            }

            CompletableFuture.supplyAsync {
                this.saveAllQrCode(successQrCodes.toSet())
            }
            transactionService.saveAllTransactions(requests)
        }
    }

    fun saveWhenTimeout(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            val qrCodes = this.getAllQrCodeByIds(ids)
            val timeoutQrCodes = mutableSetOf<Long>()
            val pendingQrCodeIds = mutableSetOf<Long>()

            qrCodes.map {
                if (it.timeoutAt.isBefore(LocalDateTime.now())) {
                    timeoutQrCodes.add(it.id)
                } else {
                    pendingQrCodeIds.add(it.id)
                }
            }

            if (pendingQrCodeIds.isNotEmpty()) LOG.info(">>> The QR Code id is in tracking: $pendingQrCodeIds")

            if (timeoutQrCodes.isNotEmpty()) this.saveToTimeoutQrCodes(timeoutQrCodes)
        }
    }

    fun saveToTimeoutQrCodes(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            LOG.info(">>> Time out QR Code id: $ids")
            val status = getOrElseThrow("QR Code Status", QrCodeStatusEnum.TIME_OUT.id, qrCodeStatusRepo::findById)
            val timeoutQrCode = this.getAllQrCodeByIds(ids).map {
                it.status = status
                it
            }

            this.saveAllQrCode(timeoutQrCode.toSet())
        }
    }

    fun saveToFailQrCodes(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            LOG.info(">>> Failed tracking QR Code id: $ids")
            val status = getOrElseThrow("QR Code Status", QrCodeStatusEnum.FAILED.id, qrCodeStatusRepo::findById)
            val successQrCodes = this.getAllQrCodeByIds(ids).map {
                it.status = status
                it
            }

            this.saveAllQrCode(successQrCodes.toSet())
        }
    }

    fun getAllPendingQrCode() : List<QrCode> {
        return qrCodeRepo.findAllByStatusId(QrCodeStatusEnum.PENDING.id)
    }

    @Transactional
    private fun saveAllQrCode(qrCodes: Set<QrCode>) {
        qrCodeRepo.saveAll(qrCodes)
        LOG.info(">>> Saved All QR code: ${qrCodes.size} qrCode(s)")
    }

    private fun getAllQrCodeByIds(ids: Set<Long>): List<QrCode> {
        return qrCodeRepo.findAllByIdIn(ids)
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