package org.wagham.kabotapi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()

    @Bean
    fun ktorClient(): HttpClient = HttpClient(CIO)

}