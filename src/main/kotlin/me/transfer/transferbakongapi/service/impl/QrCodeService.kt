package me.transfer.transferbakongapi.service.impl

import kh.org.nbc.bakong_khqr.model.KHQRCurrency
import kh.org.nbc.bakong_khqr.model.MerchantInfo
import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.command.Constants
import me.transfer.transferbakongapi.command.enum.CurrencyEnum
import me.transfer.transferbakongapi.command.enum.QrCodeStatusEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.demain.model.CurrencyType
import me.transfer.transferbakongapi.demain.model.QrCode
import me.transfer.transferbakongapi.demain.projection.QrCodeProjection
import me.transfer.transferbakongapi.repository.QrCodeRepository
import me.transfer.transferbakongapi.repository.QrCodeStatusRepository
import me.transfer.transferbakongapi.service.IQrCodeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

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
            terminalLabel = terminalLabel,
            retryAttempted = 0
        ).apply {
            this.currency = currency
            this.status = qrCodeStatus
        }

        return qrCodeRepo.save(qrCode)
    }

    fun saveToSuccessQrCodes(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            val status = getOrElseThrow("QR Code Status", QrCodeStatusEnum.SUCCESS.id, qrCodeStatusRepo::findById)
            val successQrCodes = this.getAllQrCodeByIds(ids).map {
                it.status = status
                it
            }

            this.saveAllQrCode(successQrCodes.toSet())
        }
    }

    fun saveWithIncreaseRetryAttempt(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            val qrCodes = this.getAllQrCodeByIds(ids)
            val timeoutQrCodes = mutableSetOf<Long>()
            val pendingQrCodeIds = mutableSetOf<Long>()

            qrCodes.map {
                if (it.retryAttempted > 60 / 2) {
                    timeoutQrCodes.add(it.id)
                } else {
                    pendingQrCodeIds.add(it.id)
                }
            }

            if (pendingQrCodeIds.isNotEmpty()) {
                val pendingQrCodes = this.getAllQrCodeByIds(pendingQrCodeIds).map {
                    it.retryAttempted = it.retryAttempted.plus(1)
                    it
                }

                this.saveAllQrCode(pendingQrCodes.toSet())
            }

            if (timeoutQrCodes.isNotEmpty()) this.saveToTimeoutQrCodes(timeoutQrCodes)
        }
    }

    fun saveToTimeoutQrCodes(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
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
            val status = getOrElseThrow("QR Code Status", QrCodeStatusEnum.FAILED.id, qrCodeStatusRepo::findById)
            val successQrCodes = this.getAllQrCodeByIds(ids).map {
                it.status = status
                it
            }

            this.saveAllQrCode(successQrCodes.toSet())
        }
    }

    fun getAllPendingQrCode() : List<QrCodeProjection> {
        return qrCodeRepo.findAllByStatusId(QrCodeStatusEnum.PENDING.id, QrCodeProjection::class.java)
    }

    @Transactional
    private fun saveAllQrCode(qrCodes: Set<QrCode>) {
        qrCodeRepo.saveAll(qrCodes)
        LOG.info(">>> Saved All QR code: ${qrCodes.size} qrCode(s)")
    }

    private fun getAllQrCodeByIds(ids: Set<Long>): List<QrCode> {
        return qrCodeRepo.findAllByIdIn(ids)
    }

//    @Retryable(
//        value = [RuntimeException::class],
//        maxAttempts = Constants.MAX_RETRY_ATTEMPT_BAKONG_QR_TRANSACTION,
//        backoff = Backoff(delay = Constants.BAKONG_QR_TRANSACTION_RETRY_IN)
//    )
//    fun trackingQrTransactionStatus(qrCode: QrCode) {
//        val retryCount = RetrySynchronizationManager.getContext().retryCount + 1
//        val logDescription = "Tracking Transaction status of QR Code [${qrCode.id}], md5 [${qrCode.md5}]"
//        LOG.info("Retry[$retryCount] - $logDescription")
//
//        val updateQrCode = getOrElseThrow("QrCode", qrCode.id, qrCodeRepo::findById)
//
//        if (updateQrCode.status.id != QrCodeStatusEnum.PENDING.id) {
//            LOG.info("Stop retry for QR Code [${qrCode.id}], md5 [${qrCode.md5}]")
//            return
//        }
//
//        val res = bakongOpenAPIClientHelper.checkTransactionWithMd5(updateQrCode.md5)
//        val data = res.data
//
//        when (res.responseCode) {
//            ResponseCode.SUCCESS.code -> {
//                if (data != null) {
//                    LOG.info("Transaction Success hash: ${data.hash}")
//                    updateQrCodeStatus(updateQrCode, QrCodeStatusEnum.SUCCESS.id, retryCount)
//                    CompletableFuture.supplyAsync {
//                        transactionService.createTransaction(updateQrCode, data)
//                    }
//                } else {
//                    LOG.info("Success but no data")
//                    throw java.lang.RuntimeException("try_check_again")
//                }
//            }
//            ResponseCode.FAIL.code -> {
//                when (res.errorCode) {
//                    ErrorCode.TRANSACTION_NOT_FOUND.code -> {
//                        LOG.info("Not found transaction -> Try again")
//                        throw java.lang.RuntimeException("try_check_again")
//                    }
//                    ErrorCode.TRANSACTION_FAILED.code -> {
//                        LOG.info("Transaction Failed")
//                        updateQrCodeStatus(updateQrCode, QrCodeStatusEnum.FAILED.id, retryCount)
//                    }
//                    else -> {
//                        LOG.info("Unknown error code")
//                        throw java.lang.RuntimeException("try_check_again")
//                    }
//                }
//            }
//        }
//    }

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