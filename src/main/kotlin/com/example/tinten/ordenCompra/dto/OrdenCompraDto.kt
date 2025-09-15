package com.example.tinten.ordenCompra.dto

import com.example.tinten.producto.dto.ProductoDto
import java.math.BigDecimal
import java.time.LocalDate

data class OrdenCompraDto(
    val id: Long?,
    val proveedorRazonSocial: String,
    val atencionDe: String,
    val fecha: LocalDate,
    val moneda: String,
    val productos: List<ProductoOrdenCompraDto>,
    val transporte: String?,
    val domicilio: String?,
    val fechaCreacion: LocalDate
)






