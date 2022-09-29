package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.repository.TransactionRepository
import me.transfer.transferbakongapi.service.ITransactionService
import org.springframework.stereotype.Service

@Service
class TransactionService(
  private val transactionRepo: TransactionRepository
) : ITransactionService {
}