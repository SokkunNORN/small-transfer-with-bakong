package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.model.QrCodeStatus
import org.springframework.data.jpa.repository.JpaRepository

interface QrCodeStatusRepository: JpaRepository<QrCodeStatus, Long>