package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.repository.TransactionStatusRepository
import me.transfer.transferbakongapi.service.ITransactionStatusService
import org.springframework.stereotype.Service

@Service
class TransactionStatusService(
    private val transactionStatusRepo: TransactionStatusRepository
) : ITransactionStatusService {
}