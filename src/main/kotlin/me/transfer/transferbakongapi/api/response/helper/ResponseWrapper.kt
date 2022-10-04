package me.transfer.transferbakongapi.api.response.helper

import me.transfer.transferbakongapi.api.exception.GeneralErrorException
import me.transfer.transferbakongapi.command.ErrorCode


data class ResponseWrapper<T>(val status: Status, val data: T?) {
    companion object {
        fun <T> data(data: T): ResponseWrapper<T> {
            return ResponseWrapper(Status.SUCCESSFUL, data)
        }

        fun error(error: ErrorCode): ResponseWrapper<Any> {
            val status = Status(error.code, error.message)
            return ResponseWrapper(status, null)
        }

        fun error(error: ErrorCode, message: String): ResponseWrapper<Any> {
            val status = Status(error.code, String.format(error.message, message).trim())
            return ResponseWrapper(status, null)
        }

        fun error(error: ErrorCode, firstParam: String, secondParam: String, thirdParam: String): ResponseWrapper<Any> {
            val status = Status(error.code, String.format(error.message, firstParam, secondParam, thirdParam).trim())
            return ResponseWrapper(status, null)
        }
    }

    class Status(val errorCode: Number, val errorMessage: String) {
        companion object {
            val SUCCESSFUL = Status(ErrorCode.SUCCESSFUL.code, ErrorCode.SUCCESSFUL.message)
        }
    }
}

fun <T> ok(data: T): ResponseWrapper<T> = ResponseWrapper.data(data)
fun err(message: String): Nothing = throw GeneralErrorException(message)
