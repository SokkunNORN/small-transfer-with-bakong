package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.repository.QrCodeStatusRepository
import me.transfer.transferbakongapi.service.IQrCodeStatusService
import org.springframework.stereotype.Service

@Service
class QrCodeStatusService(
    private val qrCodeStatusRepo: QrCodeStatusRepository
) : IQrCodeStatusService {
}