package org.wagham.kabotapi

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.wagham.kabotapi.exceptions.UnauthorizedException
import reactor.core.publisher.Mono
import java.io.IOException

@Component
class GlobalErrorHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable) = exchange.response.let { r ->

        val bufferFactory = r.bufferFactory()

        r.headers.contentType = MediaType.APPLICATION_JSON
        r.writeWith(
            Mono.just(
                when (ex) {
                    is IOException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.BAD_REQUEST }
                    is JwtException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.UNAUTHORIZED }
                    is IllegalArgumentException -> bufferFactory.toBuffer(ex.message)
                        .also { r.statusCode = HttpStatus.BAD_REQUEST }
                    is AccessDeniedException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.FORBIDDEN }
                    is ServerWebInputException -> bufferFactory.toBuffer(ex.reason).also { r.statusCode = HttpStatus.BAD_REQUEST }
                    is UnauthorizedException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.UNAUTHORIZED }
                    is BadJwtException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.UNAUTHORIZED }
                    else -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.INTERNAL_SERVER_ERROR }
                }
            )
        )
    }

    private fun DataBufferFactory.toBuffer(info: String?) = try {
        val error = info?.let { HttpError(it) } ?: "Unknown error".toByteArray()
        this.wrap(objectMapper.writeValueAsBytes(error))
    } catch (e: JsonProcessingException) {
        this.wrap("".toByteArray())
    }

    class HttpError internal constructor(val message: String)

}