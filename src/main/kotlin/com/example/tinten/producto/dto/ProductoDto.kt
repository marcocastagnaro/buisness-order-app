package com.example.tinten.producto.dto

import java.time.LocalDate

data class ProductoDto(
    val id: Long?,
    val nombre: String,
    val marca: String,
    val proveedor: String,
    val envase: String,
    val createdAt: LocalDate,
)

