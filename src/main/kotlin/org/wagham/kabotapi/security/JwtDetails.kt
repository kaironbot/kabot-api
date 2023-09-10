package org.wagham.kabotapi.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtDetails(
    private val authorities: Set<GrantedAuthority>,
    val userId: String,
    val guildId: String,
    val discordAuthToken: String,
    val discordRefreshToken: String? = null
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities.toMutableSet()
    override fun getPassword(): String = ""
    override fun getUsername(): String = ""
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = false
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}