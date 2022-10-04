package me.transfer.transferbakongapi.api.bakong_client.dto

object RenewTokenDto {
    data class Request(
        val email: String
    )

    data class Response(
        val token: String,
    )
}