package me.transfer.transferbakongapi.api.bakong_client.open.helper

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.api.bakong_client.dto.RenewTokenDto
import me.transfer.transferbakongapi.api.bakong_client.dto.ResponseWrapper
import me.transfer.transferbakongapi.api.bakong_client.open.BakongOpenAPIClient
import me.transfer.transferbakongapi.service.system_setting.impl.SystemSettingService
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BakongOpenAPIClientHelper(
    private val bakongOpenAPIClient: BakongOpenAPIClient,
    private val systemSettingService: SystemSettingService
) {

    private fun getBearerToken(): String {
        val systemSetting = systemSettingService.getSystemSetting()
        var token = systemSetting.bakongOpenToken
        if (systemSetting.bakongOpenTokenExpiredAt <= LocalDateTime.now().minusDays(1)) {
            token = renewToken()?.token.toString()
        }

        return "Bearer $token"
    }

    @Retryable(value = [RuntimeException::class], maxAttempts = 3, backoff = Backoff(delay = 1000))
    fun renewToken(): RenewTokenDto.Response? {
        val systemSetting = systemSettingService.getSystemSetting()
        val response = bakongOpenAPIClient.renewToken(RenewTokenDto.Request(email = systemSetting.bakongOpenEmail))
        if (response.data == null) throw java.lang.RuntimeException("try_again")

        systemSettingService.updateBakongOpenAPIToken(response.data.token)
        return response.data
    }

    fun checkTransactionWithMd5(md5: String): ResponseWrapper<CheckTransactionMd5Dto.Response> {
        return bakongOpenAPIClient.checkTransactionWithMd5(
            authorizationHeader = getBearerToken(),
            request = CheckTransactionMd5Dto.Request(
                md5 = md5
            )
        )
    }

}