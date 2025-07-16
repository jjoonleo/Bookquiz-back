package kr.co.bookquiz.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import kr.co.bookquiz.api.config.JwtProperties

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class ApiApplication
fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}