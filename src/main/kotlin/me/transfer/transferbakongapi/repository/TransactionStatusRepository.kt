package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.demain.model.TransactionStatus
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionStatusRepository: JpaRepository<TransactionStatus, Long>