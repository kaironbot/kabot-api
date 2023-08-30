package org.wagham.kabotapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("kaironbot.discord")
class DiscordConfiguration {

    var clientId: String = ""
    var clientSecret: String = ""
    var redirectUrl: String = ""
    var apiEndpoint: String = "https://discord.com/api/v10"

}