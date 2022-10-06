package me.transfer.transferbakongapi.api.request

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.demain.model.QrCode

data class QrCodeBakongTransactionReq(
    val qrCode: QrCode,
    val bakongTrx: CheckTransactionMd5Dto.Response
)
