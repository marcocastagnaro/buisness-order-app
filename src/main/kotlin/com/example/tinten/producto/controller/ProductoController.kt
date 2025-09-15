package com.example.tinten.producto.controller

import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductoDto
import com.example.tinten.producto.service.ProductoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@RestController
@RequestMapping("/api/productos")
class ProductoController(
    private val productoService: ProductoService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: ProductoCreateRequest): ResponseEntity<ProductoDto> {
        val producto = productoService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(producto)
    }

    @GetMapping
    fun findAll(pageable: Pageable): Page<ProductoDto> {
        return productoService.getAllProductos(pageable)
    }

    @GetMapping("/all")
    fun findAllWitoutPage(): ResponseEntity<List<ProductoDto>> {
        val productos = productoService.getAllProductosWithoutPage()
        return ResponseEntity.ok(productos)
    }
    @GetMapping("/{productoId}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductoDto> {
        val producto = productoService.getProductoById(id)
        return ResponseEntity.ok(producto)
    }
}