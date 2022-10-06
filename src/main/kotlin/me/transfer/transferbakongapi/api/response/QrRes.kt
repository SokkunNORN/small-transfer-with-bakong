package me.transfer.transferbakongapi.api.response

import me.transfer.transferbakongapi.demain.model.CurrencyType

data class QrRes(
    val amount: Double,
    val qrString: String,
    val md5: String,
    val currency: CurrencyType,
    val terminalLabel: String
)