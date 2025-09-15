package com.example.tinten.producto.service

import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductoDto
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.repository.ProductoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Reutilizá tus propias excepciones si ya existen
class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)

@Service
class ProductoService(
    @Autowired
    private val productoRepository: ProductoRepository
) {

    fun create(request: ProductoCreateRequest): ProductoDto {
        validarCreate(request)

        val normalized = request.copy(
            nombre = request.nombre.trim(),
            marca = request.marca.trim(),
            proveedor = request.proveedor.trim()
        )

        if (existeNombre(normalized.nombre)) {
            throw ValidationException("Ya existe un producto con nombre '${normalized.nombre}'")
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
        if (req.nombre.isBlank()) throw ValidationException("El nombre es obligatorio")
        if (req.marca.isBlank()) throw ValidationException("La marca es obligatoria")
        if (req.proveedor.isBlank()) throw ValidationException("El proveedor es obligatorio")

        if (req.nombre.length > 120) throw ValidationException("El nombre excede 120 caracteres")
        if (req.marca.length > 80) throw ValidationException("La marca excede 80 caracteres")
        if (req.proveedor.length > 120) throw ValidationException("El proveedor excede 120 caracteres")

        val basicRegex = Regex("""^[\p{L}\p{N}\s\-\._&/()]+$""") // letras, números, espacio y algunos símbolos comunes
        if (!basicRegex.matches(req.nombre.trim())) throw ValidationException("El nombre contiene caracteres inválidos")
        if (!basicRegex.matches(req.marca.trim())) throw ValidationException("La marca contiene caracteres inválidos")
        if (!basicRegex.matches(req.proveedor.trim())) throw ValidationException("El proveedor contiene caracteres inválidos")
    }

    private fun existeNombre(nombre: String): Boolean {
        val existente = productoRepository.findByNombre(nombre)
        if (existente.isPresent) return true

        return productoRepository.findAll()
            .any { it.nombre.equals(nombre, ignoreCase = true) }
    }
}
