package me.transfer.transferbakongapi.demain.model

import javax.persistence.*

@Entity
@Table(name = "currency_type")
data class CurrencyType(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "code", nullable = false)
    val code: String
)
