package me.transfer.transferbakongapi.api.bakong_client.enum

enum class ErrorCode(val code: Int, val description: String) {
    TRANSACTION_NOT_FOUND(1, "Transaction could not be found. Please try again."),
    NOT_SUPPORT_STATIC_QR_CODE(2, "Sorry, the system does not support static QR code."),
    TRANSACTION_FAILED(3, "Transaction failed."),
    ERROR_DEEPLINK_PROVIDER(4, "Error occurred on requesting deeplink from provider."),

    MISSING_REQUIRED_FIELDS(5, "Missing required fields."),
    UNAUTHORIZED(6, "Unauthorized"),
    EMAIL_SERVER_DOWN(7, "Email server has been down."),
    EMAIL_REGISTERED_ALREADY(8, "Email has been registered already."),

    CANNOT_CONNECT_TO_SERVER(9, "Cannot connect to server. Please try again later."),
    NOT_REGISTER_YET(10, "Not registered yet."),
    ACCOUNT_ID_NOT_FOUND(11, "Account ID not found."),
    ACCOUNT_ID_INVALID(12, "Account ID is invalid."),
}
