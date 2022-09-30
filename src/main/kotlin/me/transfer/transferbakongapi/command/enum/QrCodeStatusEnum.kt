package me.transfer.transferbakongapi.command.enum

enum class QrCodeStatusEnum(val id: Long, val description: String) {
    PENDING(1, "Pending"),
    FAILED(2, "Failed"),
    SUCCESS(3, "Success"),
    CANCELLED(4, "Cancelled"),
    DELETED(5, "Deleted"),
    TIME_OUT(6, "Timeout"),
    FAILED_CREATE_TRANSACTION(7, "Failed Create Transaction");

    companion object {
        fun ids(): LongArray {
            return values().map { it.id }.toLongArray()
        }
    }
}