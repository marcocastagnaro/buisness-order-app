package com.example.tinten.ordenCompra.controller

import com.example.tinten.ordenCompra.dto.*
import com.example.tinten.ordenCompra.service.OrdenCompraPdfService
import com.example.tinten.ordenCompra.service.OrdenCompraService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api/ordenescompra")
class OrdenCompraController(
    private val ordenCompraService: OrdenCompraService,
    private val pdfService: OrdenCompraPdfService

) {
    
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<Page<OrdenCompraDto>> {
        val sort = if (sortDir == "desc") Sort.by(sortBy).descending() else Sort.by(sortBy).ascending()
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val ordenesCompra = ordenCompraService.findAll(pageable)
        return ResponseEntity.ok(ordenesCompra)
    }
    
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<OrdenCompraDto> {
        val ordenCompra = ordenCompraService.findById(id)
        return ResponseEntity.ok(ordenCompra)
    }
    
    @PostMapping
    fun create(@Valid @RequestBody request: OrdenCompraCreateRequest): ResponseEntity<OrdenCompraDto> {
        val ordenCompra = ordenCompraService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompra)
    }
    
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrdenCompraUpdateRequest
    ): ResponseEntity<OrdenCompraDto> {
        val ordenCompra = ordenCompraService.updateOrdenCompra(id, request)
        return ResponseEntity.ok(ordenCompra)
    }
    
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        ordenCompraService.delete(id)
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