package me.transfer.transferbakongapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableRetry
class TransferBakongApiApplication

fun main(args: Array<String>) {
	runApplication<TransferBakongApiApplication>(*args)
}
