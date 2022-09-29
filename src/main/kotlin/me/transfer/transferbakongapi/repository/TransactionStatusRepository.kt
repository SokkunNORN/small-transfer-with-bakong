package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.model.TransactionStatus
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionStatusRepository: JpaRepository<TransactionStatus, Long>