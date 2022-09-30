package me.transfer.transferbakongapi.model

import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "qr_codes")
data class QrCode(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "qr_string", nullable = false)
    val qrString: String,

    @Column(name = "md5", nullable = false)
    val md5: String,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal = BigDecimal(0),

    @Column(name = "bill_number")
    val billNumber: String? = null,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "retry_attempted", nullable = false)
    val retryAttempted: Long = 0L,

    @Column(name = "terminal_label")
    val terminalLabel: String? = null,

    @Column(name = "cashier_label")
    val cushierLabel: String? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @ManyToOne
    @JoinColumn(name = "currency_id")
    lateinit var currency: CurrencyType

    @ManyToOne
    @JoinColumn(name = "status_id")
    lateinit var status: QrCodeStatus
}
