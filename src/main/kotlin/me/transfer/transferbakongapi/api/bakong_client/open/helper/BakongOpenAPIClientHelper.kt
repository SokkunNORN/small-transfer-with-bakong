package me.transfer.transferbakongapi.api.bakong_client.open.helper

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.api.bakong_client.dto.RenewTokenDto
import me.transfer.transferbakongapi.api.bakong_client.dto.ResponseWrapper
import me.transfer.transferbakongapi.api.bakong_client.open.BakongOpenAPIClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class BakongOpenAPIClientHelper(
    private val bakongOpenAPIClient: BakongOpenAPIClient
) {

    private fun getBearerToken(): String {
        return "Bearer "
    }

    @Retryable(value = [RuntimeException::class], maxAttempts = 3, backoff = Backoff(delay = 1000))
    fun renewToken(request: RenewTokenDto.Request): RenewTokenDto.Response? {
        val response = bakongOpenAPIClient.renewToken(RenewTokenDto.Request(email = "systemSetting.bakongOpenEmail"))
        if (response.data == null) throw java.lang.RuntimeException("try_again")

        return response.data
    }

    fun checkTransactionWithMd5(
        authorizationHeader: String,
        request: CheckTransactionMd5Dto.Request
    ): ResponseWrapper<CheckTransactionMd5Dto.Response> {
        TODO("Not yet implemented")
    }

}