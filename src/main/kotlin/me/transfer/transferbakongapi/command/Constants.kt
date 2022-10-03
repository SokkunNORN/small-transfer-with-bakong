package me.transfer.transferbakongapi.command

import java.math.BigDecimal

object Constants {
    // Currency
    val MIN_KHR_AMOUNT: BigDecimal = BigDecimal.valueOf(1)
    val MAX_KHR_AMOUNT: BigDecimal = BigDecimal.valueOf(9_999_999_999)
    val MIN_USD_AMOUNT: BigDecimal = BigDecimal.valueOf(0.01)
    val MAX_USD_AMOUNT: BigDecimal = BigDecimal.valueOf(999_999)

    const val BAKONG_OPEN_API_EXPIRES_IN_DAYS = 30L
    const val ACQUIRER_BAKONG_ACCOUNT_ID = "merchant_aquirer@devb"
    const val ACQUIRER_BAKONG_BANK_ID = "devbkhppxxx@devb"
    // TO DO - Define Acquirer Bakong Bank Name
    const val ACQUIRER_BAKONG_BANK_NAME = "Dev Bank"

    const val BAKONG_QR_TRANSACTION_RETRY_IN = 10_000L // In seconds
    const val MAX_RETRY_ATTEMPT_BAKONG_QR_TRANSACTION = 300 // 100
}