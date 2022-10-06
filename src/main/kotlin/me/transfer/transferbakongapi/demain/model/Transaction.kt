package me.transfer.transferbakongapi.demain.model

import org.hibernate.FetchMode.LAZY
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "transaction")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction")
    @SequenceGenerator(name = "seq_transaction", sequenceName = "SEQ_TRANSACTION", initialValue = 10, allocationSize = 10)
    val id: Long = 0L,

    @Column(name = "hash")
    val hash: String? = null,

    @Column(name = "reversed_hash")
    val reversedHast: String? = null,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal = BigDecimal(0),

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

    @Column(name = "is_settled", nullable = false)
    var isSettled: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updateAt: LocalDateTime? = null,

    @Column(name = "description")
    val description: String? = null
) {
    @ManyToOne
    @JoinColumn(name = "currency_id")
    lateinit var currency: CurrencyType

    @ManyToOne
    @JoinColumn(name = "status_id")
    lateinit var status: TransactionStatus
}
