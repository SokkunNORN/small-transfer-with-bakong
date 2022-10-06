package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.api.request.QrCodeBakongTransactionReq
import me.transfer.transferbakongapi.command.enum.TransactionStatusEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.demain.model.QrCode
import me.transfer.transferbakongapi.demain.model.Transaction
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
  private val transactionStatusRepo: TransactionStatusRepository
) : ITransactionService {
  private val LOG = LoggerFactory.getLogger(javaClass)

  @Transactional
  fun saveTransaction(request: QrCodeBakongTransactionReq) {
    LOG.info("Create new transaction for QR Code [${request.qrCode.id}, ${request.qrCode.md5}] - Hash[${request.bakongTrx.hash}]")
    val transactionStatus = getOrElseThrow("TransactionType", TransactionStatusEnum.PENDING.id, transactionStatusRepo::findById)

    val newTrx = Transaction(
      hash = request.bakongTrx.hash,
      amount = request.qrCode.amount,
      billNumber = request.qrCode.billNumber,
      storeLabel = request.qrCode.cushierLabel,
      terminalLabel = request.qrCode.terminalLabel,
      senderAccount = request.bakongTrx.fromAccountId,
      receiverAccount = request.bakongTrx.toAccountId,
      description = request.qrCode.description
    ).apply {
      currency = request.qrCode.currency
      status = transactionStatus
    }

    val savedTrx = transactionRepo.save(newTrx)

    LOG.info("Successfully create new transaction[${savedTrx.id}] hash[${savedTrx.hash}]")
  }

  @Transactional
  fun saveAllTransactions(requests: Set<QrCodeBakongTransactionReq>) {
    val newTransactions = mutableSetOf<Transaction>()
    val transactionStatus = getOrElseThrow("TransactionType", TransactionStatusEnum.PENDING.id, transactionStatusRepo::findById)

    requests.map { request ->
      val transaction = Transaction(
        hash = request.bakongTrx.hash,
        amount = request.qrCode.amount,
        billNumber = request.qrCode.billNumber,
        storeLabel = request.qrCode.cushierLabel,
        terminalLabel = request.qrCode.terminalLabel,
        senderAccount = request.bakongTrx.fromAccountId,
        receiverAccount = request.bakongTrx.toAccountId,
        description = request.qrCode.description
      ).apply {
        currency = request.qrCode.currency
        status = transactionStatus
      }

      newTransactions.add(transaction)
    }

    if (newTransactions.isNotEmpty()) {
      transactionRepo.saveAll(newTransactions)
      LOG.info("Successfully create new transaction: ${newTransactions.size} transaction(s)")
    }
  }

  @Transactional
  fun settleTransaction() {
    LOG.info("Settlement starting...")
    val status = getOrElseThrow("Transaction Status", TransactionStatusEnum.SUCCESS.id, transactionStatusRepo::findById)

    val transactions = transactionRepo.findAllByStatusId(TransactionStatusEnum.PENDING.id).map {
      it.status = status
      it.isSettled = true
      it
    }
    if (transactions.isNotEmpty()) transactionRepo.saveAll(transactions)

    LOG.info("Settlement ended.")
  }
}