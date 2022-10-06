package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.demain.model.QrCode
import org.springframework.data.jpa.repository.JpaRepository

interface QrCodeRepository: JpaRepository<QrCode, Long> {
    fun findAllByStatusId(statusId: Long): List<QrCode>
    fun <T> findAllByStatusId(statusId: Long, type: Class <T>): List<T>
    fun findAllByIdIn(ids: Set<Long>): List<QrCode>
}