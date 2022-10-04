package me.transfer.transferbakongapi.scheduler

import me.transfer.transferbakongapi.service.impl.TransactionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SettlementSchedule(
    private val service: TransactionService
) {

    @Scheduled(cron = "10 * * * * *")
    fun settleTransaction() = service.settleTransaction()
}