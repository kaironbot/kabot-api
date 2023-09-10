package org.wagham.kabotapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.wagham.kabotapi.exceptions.UnauthorizedException
import org.wagham.kabotapi.security.JwtAuthenticationToken
import org.wagham.kabotapi.security.JwtUtils

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtils: JwtUtils
) {

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .antMatchers("/login/discord").permitAll()
            .antMatchers("/login/refresh").permitAll()
            .anyRequest().authenticated()

        http.oauth2ResourceServer().jwt()
        http.authenticationManager { auth ->
            when(auth) {
                is BearerTokenAuthenticationToken -> {
                    val claims = jwtUtils.decodeAndGetDetails(auth.token)
                    JwtAuthenticationToken(claims.authorities.toMutableSet(), claims = claims)
                }
                is JwtAuthenticationToken -> auth
                else -> throw UnauthorizedException("Wrong jwt")
            }
        }

        http.cors()
        http.csrf().disable()

        return http.build()
    }

}