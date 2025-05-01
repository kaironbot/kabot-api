package org.wagham.kabotapi.configuration

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.request.path
import kotlin.time.Duration.Companion.seconds

const val INACTIVE_RATE_LIMIT = "inactivity"

fun Application.configureThrottling() {
	val inactivityRegex = Regex(".*/foundry/inactive/([^/]+)")
	install(RateLimit) {
		register(RateLimitName(INACTIVE_RATE_LIMIT)) {
			rateLimiter(limit = 10, refillPeriod = 60.seconds)
			requestKey { applicationCall ->
				val instanceOrNull = inactivityRegex.find(applicationCall.request.path())
					?.groupValues?.getOrNull(1)
				instanceOrNull ?: "UNKNOWN"
			}
		}
	}
}