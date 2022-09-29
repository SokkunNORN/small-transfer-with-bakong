package me.transfer.transferbakongapi.service.impl

import me.transfer.transferbakongapi.repository.QrCodeRepository
import me.transfer.transferbakongapi.service.IQrCodeService
import org.springframework.stereotype.Service

@Service
class QrCodeService(
    private val qrCodeRepo: QrCodeRepository
) : IQrCodeService {
}