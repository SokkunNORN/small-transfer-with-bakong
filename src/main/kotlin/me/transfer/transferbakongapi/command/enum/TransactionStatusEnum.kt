package me.transfer.transferbakongapi.command.enum

enum class TransactionStatusEnum(val id: Long, val description: String) {
    PENDING(1, "PENDING"),
    PROCESSING(2, "PROCESSING"),
    FAILED(3, "FAILED"),
    SUCCESS(4, "SUCCESS");

    companion object {
        fun ids(): LongArray {
            return values().map { it.id }.toLongArray()
        }

        fun successIds(): Set<Long> {
            return setOf(SUCCESS.id)
        }

        fun validReFetchStatus() = setOf(PENDING.id, PROCESSING.id)
    }
}