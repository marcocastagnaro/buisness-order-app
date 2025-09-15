package com.example.tinten.ordenCompra.dto

import java.time.LocalDate

data class OrdenCompraCreateRequest(
    val proveedorRazonSocial: String,
    val atencionDe: String,
    val fecha: LocalDate,
    val moneda: String,
    val productos: List<ProductoOrdenCompraCreateDto>, // ✅ no ProductoDto
    val transporte: String? = null,
    val domicilio: String? = null,
)