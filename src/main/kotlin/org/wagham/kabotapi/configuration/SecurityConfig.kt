package org.wagham.kabotapi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${cors.testOrigin}") val testOrigin = ""

    @Bean
    fun securityWebFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.cors()
            .and().csrf().disable()
            .authorizeRequests()
            .and().build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cors = CorsConfiguration()
        cors.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        cors.allowedOrigins = listOf(testOrigin)
        cors.allowCredentials = true
        cors.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cors)
        return source
    }
}