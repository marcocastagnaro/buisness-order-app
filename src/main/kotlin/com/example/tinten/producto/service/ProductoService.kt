package com.example.tinten.producto.service

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductoDto
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.repository.ProductoRepository
import com.example.tinten.exception.domainExceptions.ValidationExceptions
import com.example.tinten.producto.dto.ProductCompraFilterDto
import com.example.tinten.producto.dto.ProductOrderDto
import com.example.tinten.producto.dto.ProductoOrderSearchResultDto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import com.example.tinten.producto.repository.OrdenCompraItemSpecs
import com.example.tinten.producto.repository.ProductoOrdenCompraRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductoService(
    @Autowired

    private val productoRepository: ProductoRepository,
    @Autowired
    private val productOrdenCompraRepository: ProductoOrdenCompraRepository,
) {

    fun create(request: ProductoCreateRequest): ProductoDto {
        validarCreate(request)

        val normalized = request.copy(
            nombre = request.nombre.trim(),
            marca = request.marca.trim(),
            proveedor = request.proveedor.trim()
        )

        if (existeNombre(normalized.nombre)) {
            throw ValidationExceptions("Ya existe un producto con nombre '${normalized.nombre}'")
        }

        val producto = Producto.fromDto(normalized)
        val saved = productoRepository.save(producto)
        return Producto.toDto(saved)
    }

    fun getAllProductos(pageable: Pageable): Page<ProductoDto> {
        return productoRepository.findAll(pageable).map { Producto.toDto(it) }
    }
    fun getAllProductosWithoutPage(): List<ProductoDto> {
        return productoRepository.findAll().map { Producto.toDto(it) }
    }

    fun getProductoById(id: Long): ProductoDto {
        val producto = productoRepository.findById(id)
            .orElseThrow { NotFoundException("Producto no encontrado con id: $id") }
        return Producto.toDto(producto)
    }

    // ---------------------------
    // Validaciones de negocio
    // ---------------------------

    private fun validarCreate(req: ProductoCreateRequest) {
        if (req.nombre.isBlank()) throw ValidationExceptions("El nombre es obligatorio")
        if (req.marca.isBlank()) throw ValidationExceptions("La marca es obligatoria")
        if (req.proveedor.isBlank()) throw ValidationExceptions("El proveedor es obligatorio")

        if (req.nombre.length > 120) throw ValidationExceptions("El nombre excede 120 caracteres")
        if (req.marca.length > 80) throw ValidationExceptions("La marca excede 80 caracteres")
        if (req.proveedor.length > 120) throw ValidationExceptions("El proveedor excede 120 caracteres")

        val basicRegex = Regex("""^[\p{L}\p{N}\s\-\._&/()]+$""") // letras, números, espacio y algunos símbolos comunes
        if (!basicRegex.matches(req.nombre.trim())) throw ValidationExceptions("El nombre contiene caracteres inválidos")
        if (!basicRegex.matches(req.marca.trim())) throw ValidationExceptions("La marca contiene caracteres inválidos")
        if (!basicRegex.matches(req.proveedor.trim())) throw ValidationExceptions("El proveedor contiene caracteres inválidos")
    }

    private fun existeNombre(nombre: String): Boolean {
        val existente = productoRepository.findByNombre(nombre)
        if (existente.isPresent) return true

        return productoRepository.findAll()
            .any { it.nombre.equals(nombre, ignoreCase = true) }
    }
    @Transactional(readOnly = true)
    fun search(filter: ProductCompraFilterDto, pageable: Pageable): ProductoOrderSearchResultDto {
        val spec = OrdenCompraItemSpecs.withFilter(filter)

        // 1) Page de items
        val page = productOrdenCompraRepository.findAll(spec, pageable).map { it.toDto() }

        // 2) Totales del MISMO conjunto filtrado (sin paginar)
        val totals = productOrdenCompraRepository.sumTotals(spec)

        return ProductoOrderSearchResultDto(page = page, totals = totals)
    }
    private fun ProductoOrdenCompra.toDto() = ProductOrderDto(
        itemId = this.id!!,
        productoId = this.producto.id!!,
        nombreProducto = this.producto.nombre,
        marca = this.producto.marca,
        proveedor = this.producto.proveedor,
        envase = this.producto.envase,
        cantidad = this.cantidad,
        precioUnitario = this.precioUnitario,
        createdAt = this.createdAt,
        mes= this.createdAt!!.month.toString(),
    )
}
