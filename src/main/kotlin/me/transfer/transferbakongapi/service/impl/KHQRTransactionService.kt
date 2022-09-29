package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.service.IKHQRTransactionService
import org.springframework.stereotype.Service

@Service
class KHQRTransactionService(
    private val qrCodeService: QrCodeService,
    private val transactionService: TransactionService
) : IKHQRTransactionService {

}