package me.transfer.transferbakongapi.command

import me.transfer.transferbakongapi.api.exception.ClientErrorException
import me.transfer.transferbakongapi.api.exception.IdNotFoundException
import me.transfer.transferbakongapi.api.exception.ParamNotFoundException
import me.transfer.transferbakongapi.api.exception.RecordExistException
import me.transfer.transferbakongapi.api.response.helper.PageResponse
import me.transfer.transferbakongapi.api.response.helper.Pagination
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mapping.PropertyPath
import org.springframework.data.mapping.PropertyReferenceException
import java.util.*

infix fun Boolean.then(action: () -> Unit): Boolean {
    if (this)
        action.invoke()
    return this
}

fun <T> getOrElseThrow(name: String, id: Long, loader: (Long) -> Optional<T>): T {
    return loader(id).orElseThrow { throw IdNotFoundException(name, id) }
}

fun <T> getOrElseThrow(name: String, field: String, loader: (String) -> Optional<T>): T {
    return loader(field).orElseThrow { throw ParamNotFoundException("$name[$field]") }
}

fun <T> getOrElseThrow(name: String, field: T?): T {
    return field ?: throw ParamNotFoundException("The param[$name]")
}

fun <T> Optional<T>.orElseThrow(msg: String): T {
    return this.orElseThrow { throw ParamNotFoundException(msg) }
}

fun Boolean.existsAndThrow(msg: String): Boolean {
    this.then { throw RecordExistException(msg) }
    return this
}

fun Double.isContainDecimal() = !this.rem(1).equals(0.0)

// Page Response
fun <T> Pageable.checkingSortFields(type: Class<T>) {
    this.sort.toList().map {
        try {
            PropertyPath.from(it.property, type)
        } catch (e: PropertyReferenceException) {
            throw ClientErrorException(ErrorCode.UNRECOGNIZED_FIELD, e.propertyName)
        }
    }
}

fun <T> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(
        content = this.content,
        pagination = Pagination(
            currentPage = this.pageable.pageNumber,
            pageSize = this.pageable.pageSize,
            totalElements = this.totalElements,
            totalPages = this.totalPages
        )
    )
}