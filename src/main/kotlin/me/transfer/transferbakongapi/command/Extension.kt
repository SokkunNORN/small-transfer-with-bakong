package me.transfer.transferbakongapi.command

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Extension {
    fun LocalDate?.kh () : String? {
        return this?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

    fun LocalDateTime?.kh () : String? {
        return this?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))
    }
}