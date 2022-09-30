package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.model.QrCode
import org.springframework.data.jpa.repository.JpaRepository

interface QrCodeRepository: JpaRepository<QrCode, Long>