package com.example.tinten.ordenCompra.dto

import java.time.LocalDate

data class OrdenCompraUpdateRequest(
    val proveedorRazonSocial: String? = null,
    val atencionDe: String? = null,
    val fecha: LocalDate? = null,
    val moneda: String? = null,
    val productos: List<ProductoOrdenCompraCreateDto>? = null,
    val transporte: String? = null,
    val domicilio: String? = null,
)