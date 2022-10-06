package me.transfer.transferbakongapi.service.system_setting.impl

import me.transfer.transferbakongapi.command.Constants
import me.transfer.transferbakongapi.command.getOrElseThrow
import me.transfer.transferbakongapi.demain.model.SystemSetting
import me.transfer.transferbakongapi.repository.SystemSettingRepository
import me.transfer.transferbakongapi.service.system_setting.ISystemSettingService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SystemSettingService(
    private val systemSettingRepo: SystemSettingRepository
) : ISystemSettingService {

    override fun getSystemSetting(): SystemSetting {
        return getOrElseThrow("SystemSetting", 1L, systemSettingRepo::findById)
    }

    override fun updateBakongOpenAPIToken(newToken: String): SystemSetting {
        val systemSetting = getSystemSetting().apply {
            this.bakongOpenToken = newToken
            this.bakongOpenTokenExpiredAt = LocalDateTime.now().plusDays(Constants.BAKONG_OPEN_API_EXPIRES_IN_DAYS)
            this.updatedAt = LocalDateTime.now()
        }
        return systemSettingRepo.save(systemSetting)
    }

    override fun updateBakongOpenAPIRegisteredEmail(newEmail: String): SystemSetting {
        val systemSetting = getSystemSetting().apply {
            this.bakongOpenEmail = newEmail
            this.updatedAt = LocalDateTime.now()
        }
        return systemSettingRepo.save(systemSetting)
    }

    override fun updateBakongMobileAPIToken(newToken: String, expiresInSecond: Long): SystemSetting {
        val systemSetting = getSystemSetting().apply {
            this.bakongMobileToken = newToken
            this.bakongMobileTokenExpiredAt = LocalDateTime.now().plusSeconds(expiresInSecond)
            this.updatedAt = LocalDateTime.now()
        }
        return systemSettingRepo.save(systemSetting)
    }
}
