package me.transfer.transferbakongapi.model

import org.hibernate.FetchMode.LAZY
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "transaction")
data class Transaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "bakong_account_id")
    val bakongAccountId: String? = null,

    @Column(name = "account_info")
    val accountInfo: String? = null,

    @Column(name = "acquiring_bank")
    val acquiringBank: String? = null,

    @Column(name = "hash")
    val hash: String? = null,

    @Column(name = "reversed_hash")
    val reversedHast: String? = null,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal = BigDecimal(0),

    @Column(name = "merchant_name")
    val merchantName: String? = null,

    @Column(name = "merchant_city")
    val merchantCity: String? = null,

    @Column(name = "bill_number")
    val billNumber: String? = null,

    @Column(name = "store_label")
    val storeLabel: String? = null,

    @Column(name = "terminal_label")
    val terminalLabel: String? = null,

    @Column(name = "sender_account")
    val senderAccount: String? = null,

    @Column(name = "sender_name")
    val senderName: String? = null,

    @Column(name = "receiver_account")
    val receiverAccount: String? = null,

    @Column(name = "receiver_name")
    val receiverName: String? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "settled_at")
    val settledAt: LocalDateTime? = null
) {
    @ManyToOne
    @JoinColumn(name = "currency_id")
    lateinit var currency: CurrencyType

    @ManyToOne
    @JoinColumn(name = "status_id")
    lateinit var status: TransactionStatus
}
