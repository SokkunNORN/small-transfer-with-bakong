package me.transfer.transferbakongapi.api.response

import me.transfer.transferbakongapi.api.response.helper.err
import me.transfer.transferbakongapi.command.Constants
import me.transfer.transferbakongapi.command.enum.CurrencyEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.command.isContainDecimal
import me.transfer.transferbakongapi.demain.model.CurrencyType
import java.math.BigDecimal

data class QrRes(
    val amount: Double,
    val qrString: String,
    val md5: String,
    val currency: CurrencyType,
    val terminalLabel: String
)