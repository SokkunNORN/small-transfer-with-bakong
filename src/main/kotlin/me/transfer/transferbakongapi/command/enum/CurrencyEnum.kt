package me.transfer.transferbakongapi.command.enum

enum class CurrencyEnum(val id: Long, val code: String, val nameCurrency: String) {
    KHR(1, "KHR", "Khmer Riel"),
    USD(2, "USD", "United States dollar");

    companion object {
        fun ids(): LongArray {
            return values().map { it.id }.toLongArray()
        }
    }
}