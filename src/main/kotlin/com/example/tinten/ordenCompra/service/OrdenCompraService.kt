package com.example.tinten.ordenCompra.service

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.ordenCompra.dto.*
import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.ordenCompra.repository.OrdenCompraRepository
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import com.example.tinten.producto.repository.ProductoRepository
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime


@Service
class OrdenCompraService(
    @Autowired
    private val ordenCompraRepository: OrdenCompraRepository,
    @Autowired
    private val productoRepository: ProductoRepository,

) {

    fun findAll(pageable: Pageable): Page<OrdenCompraDto> {
        return ordenCompraRepository.findAll(pageable).map { it.toDto() }
    }

    fun findById(id: Long): OrdenCompraDto {
        val ordenCompra = ordenCompraRepository.findById(id)
            .orElseThrow { NotFoundException("Orden de compra no encontrada con id: $id") }
        return ordenCompra.toDto()
    }

    fun create(request: OrdenCompraCreateRequest): OrdenCompraDto {
        validarReglasDeNegocioCreate(request)

        val ordenCompra = OrdenCompra(
            proveedorRazonSocial = request.proveedorRazonSocial,
            atencionDe = request.atencionDe,
            fecha = request.fecha,
            moneda = request.moneda,
            transporte = request.transporte
        )

        request.productos.forEach { productoReq ->
            val producto = productoRepository.findById(productoReq.productoId)
                .orElseThrow { NotFoundException("Producto no encontrado: ${productoReq.productoId}") }

            val productoOrdenCompra = ProductoOrdenCompra(
                producto = producto,
                ordenCompra = ordenCompra,
                precioUnitario = productoReq.precioUnitario,
                cantidad = productoReq.cantidad
            )

            ordenCompra.productos.add(productoOrdenCompra)
        }

        val saved = ordenCompraRepository.save(ordenCompra)
        return saved.toDto()
    }


    fun updateOrdenCompra(id: Long, request: OrdenCompraUpdateRequest): OrdenCompraDto {
        val ordenCompra = ordenCompraRepository.findById(id)
            .orElseThrow { NotFoundException("OrdenCompra con id $id no encontrada") }

        validarReglasDeNegocioUpdate(request)

        // actualizar campos solo si vienen en el request
        request.proveedorRazonSocial?.let { ordenCompra.proveedorRazonSocial = it.trim() }
        request.atencionDe?.let { ordenCompra.atencionDe = it.trim() }
        request.fecha?.let { ordenCompra.fecha = it }
        request.moneda?.let { ordenCompra.moneda = it }
        request.transporte?.let { ordenCompra.transporte = it.trim() }
        // si hay productos nuevos
        request.productos?.let { nuevosProductos ->
            validarProductos(nuevosProductos)

            ordenCompra.productos.clear()
            ordenCompra.productos.addAll(
                nuevosProductos.map { prodReq ->
                    val producto = productoRepository.findById(prodReq.productoId)
                        .orElseThrow { NotFoundException("Producto no encontrado: ${prodReq.productoId}") }

                    ProductoOrdenCompra(
                        producto = Producto(id = producto.id),
                        ordenCompra = ordenCompra,
                        precioUnitario = prodReq.precioUnitario.setScale(2, RoundingMode.HALF_UP),
                        cantidad = prodReq.cantidad,
                        createdAt = LocalDateTime.now()
                    )
                }
            )
        }

        val updated = ordenCompraRepository.save(ordenCompra)
        return updated.toDto()
    }

    fun delete(id: Long) {
        if (!ordenCompraRepository.existsById(id)) {
            throw RuntimeException("Orden de compra no encontrada con id: $id")
        }
        ordenCompraRepository.deleteById(id)
    }

    fun validarReglasDeNegocioCreate(req: OrdenCompraCreateRequest) {
        if (req.productos.isEmpty()) throw ValidationException("La orden debe tener productos")
        validarProductos(req.productos)
    }

    fun validarReglasDeNegocioUpdate(req: OrdenCompraUpdateRequest) {
        req.productos?.let { validarProductos(it) }
    }

    fun validarProductos(productos: List<ProductoOrdenCompraCreateDto>) {
        val names = productos.map { it -> it.productoId }
        val duplicados = names.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        if (duplicados.isNotEmpty()) {
            throw ValidationException("Hay productos duplicados: ${duplicados.joinToString(", ")}")
        }

        productos.forEach {
            val productName = productoRepository.findById(it.productoId).orElseThrow({NotFoundException("Producto ${it.productoId} no fue encontrado")})
            if (it.cantidad <= 0) throw ValidationException("La cantidad de '${productName}' debe ser > 0")
            if (it.precioUnitario <= java.math.BigDecimal.ZERO)
                throw ValidationException("El precio unitario de '${productName}' debe ser > 0")
        }
    }
}