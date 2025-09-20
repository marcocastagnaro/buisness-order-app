package com.example.tinten.producto.dto

import java.time.LocalDate

data class ProductCompraFilterDto (
    val nombreProducto: String? = null,
    val marca: String? = null,
    val proveedor: String? = null,
    val envase: String? = null,
    val createdFrom: LocalDate? = null,
    val createdTo: LocalDate? = null,
    )
