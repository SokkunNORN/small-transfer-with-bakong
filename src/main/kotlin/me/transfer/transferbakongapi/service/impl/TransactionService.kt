package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.command.enum.TransactionStatusEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.model.QrCode
import me.transfer.transferbakongapi.model.Transaction
import me.transfer.transferbakongapi.repository.CurrencyTypeRepository
import me.transfer.transferbakongapi.repository.TransactionRepository
import me.transfer.transferbakongapi.repository.TransactionStatusRepository
import me.transfer.transferbakongapi.service.ITransactionService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TransactionService(
  private val transactionRepo: TransactionRepository,
  private val currencyTypeRepo: CurrencyTypeRepository,
  private val transactionStatusRepo: TransactionStatusRepository
) : ITransactionService {
  private val LOG = LoggerFactory.getLogger(javaClass)

  @Transactional
  fun createNewPaymentTransaction(qrCode: QrCode, bakongTrx: CheckTransactionMd5Dto.Response) {
    LOG.info("Create new transaction for QR Code [${qrCode.id}, ${qrCode.md5}] - Hash[${bakongTrx.hash}]")
    val currencyType = getOrElseThrow("currency", qrCode.currency.id, currencyTypeRepo::findById)
    val transactionStatus = getOrElseThrow("TransactionType", TransactionStatusEnum.SUCCESS.id, transactionStatusRepo::findById)

    val newTrx = Transaction(
      hash = bakongTrx.hash,
      amount = qrCode.amount,
      billNumber = qrCode.billNumber,
      storeLabel = qrCode.cushierLabel,
      terminalLabel = qrCode.terminalLabel,
      senderAccount = bakongTrx.fromAccountId,
      receiverAccount = bakongTrx.toAccountId,
      description = bakongTrx.description
    ).apply {
      currency = currencyType
      status = transactionStatus
    }

    val savedTrx = transactionRepo.save(newTrx)

    LOG.info("Successfully create new transaction[${savedTrx.id}] hash[${savedTrx.hash}]")
  }
}