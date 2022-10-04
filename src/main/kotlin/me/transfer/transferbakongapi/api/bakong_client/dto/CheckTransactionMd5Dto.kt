package me.transfer.transferbakongapi.api.bakong_client.dto

import java.math.BigDecimal

object CheckTransactionMd5Dto {
    data class Request(
        val md5: String
    )

    data class Response(
        val hash: String,
        val fromAccountId: String,
        val toAccountId: String,
        val currency: String,
        val amount: BigDecimal,
        val description: String,
        val createdDateMs: BigDecimal,
        val acknowledgedDateMs: BigDecimal
    )
}