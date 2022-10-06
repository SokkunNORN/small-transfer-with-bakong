package me.transfer.transferbakongapi.repository

import me.transfer.transferbakongapi.demain.model.CurrencyType
import org.springframework.data.jpa.repository.JpaRepository

interface CurrencyTypeRepository: JpaRepository<CurrencyType, Long>