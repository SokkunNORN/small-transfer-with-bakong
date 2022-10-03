package me.transfer.transferbakongapi.api.bakong_client.enum

enum class ResponseCode(val code: Int, val description: String) {
    SUCCESS(0, "Success"),
    FAIL(1, "Fail");
}