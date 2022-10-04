package me.transfer.transferbakongapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableFeignClients
@EnableRetry
class TransferBakongApiApplication

fun main(args: Array<String>) {
	runApplication<TransferBakongApiApplication>(*args)
}
