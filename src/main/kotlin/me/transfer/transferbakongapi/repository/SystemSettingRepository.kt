package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.model.SystemSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SystemSettingRepository : JpaRepository<SystemSetting, Long>