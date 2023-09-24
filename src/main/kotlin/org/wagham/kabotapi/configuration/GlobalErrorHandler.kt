package org.wagham.kabotapi.configuration

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebInputException
import org.wagham.kabotapi.exceptions.UnauthorizedException
import java.io.IOException

@ControllerAdvice
class GlobalErrorHandler {
    @ExceptionHandler
    fun handleBadJwtException(ex: BadJwtException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    fun handleIOException(ex: IOException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.FORBIDDEN)
    @ExceptionHandler
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<HttpError> =
        ResponseEntity(ex.toHttpError(), HttpStatus.UNAUTHORIZED)

    private fun Exception.toHttpError() =
        HttpError(
            message ?: this::class.qualifiedName ?: "Something wrong occurred"
        )
    class HttpError internal constructor(val message: String)
}