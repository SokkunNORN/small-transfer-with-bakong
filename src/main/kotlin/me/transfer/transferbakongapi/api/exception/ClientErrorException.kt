package me.transfer.transferbakongapi.api.exception

import me.transfer.transferbakongapi.command.ErrorCode

data class ClientErrorException(val errorCode: ErrorCode, val description: String?) : RuntimeException(description)