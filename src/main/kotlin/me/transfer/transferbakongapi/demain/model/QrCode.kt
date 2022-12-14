package me.transfer.transferbakongapi.demain.model

import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "qr_codes")
data class QrCode(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_qr_code")
    @SequenceGenerator(name = "seq_qr_code", sequenceName = "SEQ_QR_CODE", initialValue = 10, allocationSize = 10)
    val id: Long = 1L,

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

    @Column(name = "terminal_label")
    val terminalLabel: String? = null,

    @Column(name = "cashier_label")
    val cushierLabel: String? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "timeout_at")
    val timeoutAt: LocalDateTime = LocalDateTime.now().plusSeconds(65)
) {
    @ManyToOne
    @JoinColumn(name = "currency_id")
    lateinit var currency: CurrencyType

    @ManyToOne
    @JoinColumn(name = "status_id")
    lateinit var status: QrCodeStatus
}
