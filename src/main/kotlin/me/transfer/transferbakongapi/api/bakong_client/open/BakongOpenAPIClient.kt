package me.transfer.transferbakongapi.api.bakong_client.open

import me.transfer.transferbakongapi.api.bakong_client.dto.CheckTransactionMd5Dto
import me.transfer.transferbakongapi.api.bakong_client.dto.RenewTokenDto
import me.transfer.transferbakongapi.api.bakong_client.dto.ResponseWrapper
import me.transfer.transferbakongapi.config.feign_client.BakongOpenErrorDecoder
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import javax.validation.Valid

@FeignClient(value = "bakong-open-api", url = "https://sit-api-bakong.nbc.org.kh/v1", configuration = [BakongOpenErrorDecoder::class])
interface BakongOpenAPIClient {
    @PostMapping("/renew_token")
    fun renewToken(@Valid @RequestBody request: RenewTokenDto.Request): ResponseWrapper<RenewTokenDto.Response>

    @PostMapping("/check_transaction_by_md5")
    fun checkTransactionWithMd5(
        @RequestHeader(value = "Authorization", required = true) authorizationHeader: String,
        @RequestBody request: CheckTransactionMd5Dto.Request
    ): ResponseWrapper<CheckTransactionMd5Dto.Response>
}