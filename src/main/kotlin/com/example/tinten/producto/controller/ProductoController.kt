package com.example.tinten.producto.controller

import com.example.tinten.ordenCompra.dto.ProductoOrdenCompraDto
import com.example.tinten.producto.dto.ProductCompraFilterDto
import com.example.tinten.producto.dto.ProductOrderDto
import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductoDto
import com.example.tinten.producto.dto.ProductoOrderSearchResultDto
import com.example.tinten.producto.service.ProductoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

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

    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) nombreProducto: String?,
        @RequestParam(required = false) marca: String?,
        @RequestParam(required = false) proveedor: String?,
        @RequestParam(required = false) envase: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) createdFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) createdTo: LocalDate?,
        pageable: Pageable
    ): ProductoOrderSearchResultDto {
        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            throw IllegalArgumentException("createdFrom no puede ser posterior a createdTo")
        }
        val filter = ProductCompraFilterDto(
            nombreProducto = nombreProducto,
            marca = marca,
            proveedor = proveedor,
            envase = envase,
            createdFrom = createdFrom,
            createdTo = createdTo
        )
        return this.productoService.search(filter, pageable)
    }
}