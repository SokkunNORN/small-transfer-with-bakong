package me.transfer.transferbakongapi.command

import java.math.BigDecimal

object Constants {
    // Currency
    val MIN_KHR_AMOUNT: BigDecimal = BigDecimal.valueOf(1)
    val MAX_KHR_AMOUNT: BigDecimal = BigDecimal.valueOf(9_999_999_999)
    val MIN_USD_AMOUNT: BigDecimal = BigDecimal.valueOf(0.01)
    val MAX_USD_AMOUNT: BigDecimal = BigDecimal.valueOf(999_999)
}