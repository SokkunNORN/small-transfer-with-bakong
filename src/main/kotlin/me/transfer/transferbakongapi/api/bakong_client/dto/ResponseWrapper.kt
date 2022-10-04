package me.transfer.transferbakongapi.api.bakong_client.dto

data class ResponseWrapper<T>(val data: T? = null, val errorCode: Int? = null, val responseCode: Int, val responseMessage: String)