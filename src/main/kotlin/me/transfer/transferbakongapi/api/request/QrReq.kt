package me.transfer.transferbakongapi.api.request

import me.transfer.transferbakongapi.api.response.helper.err
import me.transfer.transferbakongapi.command.Constants
import me.transfer.transferbakongapi.command.enum.CurrencyEnum
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.command.isContainDecimal
import org.slf4j.LoggerFactory
import java.math.BigDecimal

data class QrReq (
    val currencyId: Long? = null,
    val amount: Double? = null
) {
    init {
        getOrElseThrow("currencyId", this.currencyId)
        getOrElseThrow("amount", this.amount)

        if (this.amount!!.toBigDecimal() <= BigDecimal.ZERO) err("Amount must greater than 0")
        if (this.currencyId == CurrencyEnum.KHR.id) {
            if (amount.toBigDecimal() < Constants.MIN_KHR_AMOUNT) err("Amount can't less than minimum limit ${Constants.MIN_KHR_AMOUNT}")
            if (amount.toBigDecimal() >= Constants.MAX_KHR_AMOUNT) err("Amount has reach maximum limit ${Constants.MAX_KHR_AMOUNT}")
            if (this.amount.isContainDecimal()) err("${CurrencyEnum.KHR.code} Amount must not contain decimal")
        } else if (this.currencyId == CurrencyEnum.USD.id) {
            if (amount.toBigDecimal() < Constants.MIN_USD_AMOUNT) err("Amount can't less than minimum limit ${Constants.MIN_USD_AMOUNT}")
            if (amount.toBigDecimal() >= Constants.MAX_USD_AMOUNT) err("Amount has reach maximum limit ${Constants.MAX_USD_AMOUNT}")
        }
    }
}