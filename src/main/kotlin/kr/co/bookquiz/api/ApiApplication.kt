package kr.co.bookquiz.api

import kr.co.bookquiz.api.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class ApiApplication
fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}