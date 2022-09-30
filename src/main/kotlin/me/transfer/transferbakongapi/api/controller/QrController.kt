package me.transfer.transferbakongapi.api.controller

import me.transfer.transferbakongapi.api.request.QrReq
import me.transfer.transferbakongapi.api.response.helper.ok
import me.transfer.transferbakongapi.service.impl.KHQRTransactionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/qr")
class QrController(
    private val service: KHQRTransactionService
) {
    @PostMapping("/generate")
    fun generateQr(@Valid @RequestBody request: QrReq) = ok(service.generateQr(request))
}