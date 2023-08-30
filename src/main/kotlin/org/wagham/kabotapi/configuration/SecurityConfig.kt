package org.wagham.kabotapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.wagham.kabotapi.security.JwtAuthenticationToken
import org.wagham.kabotapi.security.JwtUtils

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtils: JwtUtils
) {

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf().disable()
            .cors().and()
            .authorizeRequests()
            .antMatchers("/**").permitAll()
            .and()
            .authenticationManager { auth ->
                val jwt = auth as BearerTokenAuthentication
                val claims = jwtUtils.decodeAndGetClaims(jwt.token.tokenValue).let { jwtUtils.jwtDetailsFromClaims(it) }
                JwtAuthenticationToken(claims.authorities.toMutableSet(), claims = claims)
            }.build()
    }

}