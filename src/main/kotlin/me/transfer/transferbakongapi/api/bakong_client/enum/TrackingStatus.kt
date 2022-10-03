package me.transfer.transferbakongapi.api.bakong_client.enum

enum class TrackingStatus(val type: String) {
    RECEIVE_AT_ACH("RECEIVE_AT_ACH"),
    RECEIVE_AT_RECEIVER_BANK("RECEIVE_AT_RECEIVER_BANK"),
    ACKNOWLEDGED_BY_FI("ACKNOWLEDGED_BY_FI"),
    REFUNDED_BY_RECEIVER("REFUNDED_BY_RECEIVER");
}
