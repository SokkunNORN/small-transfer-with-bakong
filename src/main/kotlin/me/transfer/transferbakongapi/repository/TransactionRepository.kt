package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository: JpaRepository<Transaction, Long> {
    fun findAllByStatusId(statusId: Long) : List<Transaction>
}