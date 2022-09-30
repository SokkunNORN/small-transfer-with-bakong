package me.transfer.transferbakongapi.model

import javax.persistence.*

@Entity
@Table(name = "qr_code_status")
data class QrCodeStatus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "description")
    val description: String?
)
