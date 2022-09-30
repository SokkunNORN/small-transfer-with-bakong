package me.transfer.transferbakongapi.api.exception

class IdNotFoundException(name: String, id: Long) : RuntimeException("The $name id[$id]")