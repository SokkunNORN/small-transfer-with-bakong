package me.transfer.transferbakongapi.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "system_setting")
data class SystemSetting(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "bakong_mobile_token")
    var bakongMobileToken: String? = "",

    @Column(name = "bakong_mobile_token_expired_at")
    var bakongMobileTokenExpiredAt: LocalDateTime? = null,

    @Column(name = "bakong_open_email")
    var bakongOpenEmail: String,

    @Column(name = "bakong_open_token")
    var bakongOpenToken: String,

    @Column(name = "bakong_open_token_expired_at")
    var bakongOpenTokenExpiredAt: LocalDateTime,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
