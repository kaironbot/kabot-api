package org.wagham.kabotapi.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

data class JwtAuthenticationToken(
    val authorities: MutableSet<GrantedAuthority> = mutableSetOf(),
    val claims: JwtDetails,
    val details: Map<String, Any> = mapOf(),
    private var authenticated: Boolean = false
): Authentication {
    override fun getName(): String = "jwt"
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getCredentials(): Any = claims
    override fun getDetails(): Any = details
    override fun getPrincipal(): Any = claims
    override fun isAuthenticated(): Boolean = authenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}