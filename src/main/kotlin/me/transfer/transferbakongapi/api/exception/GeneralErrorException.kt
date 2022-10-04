package me.transfer.transferbakongapi.api.exception

data class GeneralErrorException(val description: String?) : RuntimeException(description)