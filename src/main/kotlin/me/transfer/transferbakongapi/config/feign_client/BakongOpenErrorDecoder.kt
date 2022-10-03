package me.transfer.transferbakongapi.config.feign_client

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.ErrorDecoder
import me.transfer.transferbakongapi.config.exception.BakongOpenServerUnreachableException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class BakongOpenErrorDecoder : ErrorDecoder {

    private val errorDecoder: ErrorDecoder = ErrorDecoder.Default()
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun decode(methodKey: String?, response: Response?): Exception {
        try {
            response!!.body()
                .asInputStream().use { bodyIs ->
                    val mapper = ObjectMapper()
                }
        } catch (e: IOException) {
            log.info("Error log: ${e.message} ")
            return BakongOpenServerUnreachableException("Server Error")
        }

        return when (response.status()) {
            in 200..299 -> errorDecoder.decode(methodKey, response)
            in 300..399 -> BakongOpenServerUnreachableException("Http status ranges 3xx")
            405 -> BakongOpenServerUnreachableException("Method Not Allow 405")
            in 400..499 -> BakongOpenServerUnreachableException("Http status ranges 4xx")
            in 500..599 -> BakongOpenServerUnreachableException("Http status ranges 5xx")
            else -> BakongOpenServerUnreachableException("Server Error")
        }
    }
}