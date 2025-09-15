package com.example.tinten.ordenPago.controller

import com.example.tinten.ordenCompra.service.OrdenCompraPdfService
import com.example.tinten.ordenPago.dto.*
import com.example.tinten.ordenPago.service.OrdenPagoPdfService
import com.example.tinten.ordenPago.service.OrdenPagoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api/ordenespago")
class OrdenPagoController(
    private val ordenPagoService: OrdenPagoService,
    private val pdfService: OrdenPagoPdfService,

    ) {
    
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<Page<OrdenPagoDto>> {
        val sort = if (sortDir == "desc") Sort.by(sortBy).descending() else Sort.by(sortBy).ascending()
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val ordenesPago = ordenPagoService.findAll(pageable)
        return ResponseEntity.ok(ordenesPago)
    }
    
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<OrdenPagoDto> {
        val ordenPago = ordenPagoService.findById(id)
        return ResponseEntity.ok(ordenPago)
    }
    
    @PostMapping
    fun create(@Valid @RequestBody request: CreateOrdenPagoDto): ResponseEntity<OrdenPagoDto> {
        val ordenPago = ordenPagoService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenPago)
    }
    
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrdenPagoUpdateRequest
    ): ResponseEntity<OrdenPagoDto> {
        val ordenPago = ordenPagoService.update(id, request)
        return ResponseEntity.ok(ordenPago)
    }
    
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        ordenPagoService.delete(id)
        return ResponseEntity.noContent().build()
    }
    @GetMapping("/{id}/export", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun exportPdf(@PathVariable id: Long): ResponseEntity<ByteArrayResource> {
        val pdf = pdfService.exportarPdf(id)
        val filename = "orden-compra-$id.pdf"

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdf.size.toLong())
            .body(ByteArrayResource(pdf))
    }
} 