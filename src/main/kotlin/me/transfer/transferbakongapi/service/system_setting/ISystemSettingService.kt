package me.transfer.transferbakongapi.service.system_setting

import me.transfer.transferbakongapi.model.SystemSetting

interface ISystemSettingService {

    fun getSystemSetting(): SystemSetting
    fun updateBakongOpenAPIToken(newToken: String): SystemSetting
    fun updateBakongOpenAPIRegisteredEmail(newEmail: String): SystemSetting
    fun updateBakongMobileAPIToken(newToken: String, expiresInSecond: Long): SystemSetting
}
