package me.transfer.transferbakongapi.service

import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.api.response.QrRes

interface IKHQRTransactionService {
    fun generateQr(request: QrReq): QrRes
}