package com.example.tinten.exception


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import com.example.tinten.exception.domainExceptions.NotFoundException
import jakarta.validation.ValidationException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to ex.message))

    @ExceptionHandler(ValidationException::class, IllegalArgumentException::class)
    fun handleValidation(ex: RuntimeException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleBeanValidation(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "inválido") }
        return ResponseEntity.badRequest().body(mapOf("message" to "Validación fallida", "errors" to errors))
    }
}
