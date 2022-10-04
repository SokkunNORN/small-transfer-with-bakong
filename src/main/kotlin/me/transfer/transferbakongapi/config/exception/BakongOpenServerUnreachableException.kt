package me.transfer.transferbakongapi.config.exception

import me.transfer.transferbakongapi.command.ErrorCode

class BakongOpenServerUnreachableException(override val message: String? = ErrorCode.BAKONG_OPEN_SERVER_NOT_REACHABLE.message) : RuntimeException(message)