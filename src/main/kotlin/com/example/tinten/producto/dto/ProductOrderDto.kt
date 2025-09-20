package com.example.tinten.producto.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductOrderDto (
    val itemId: Long,
    val productoId: Long,
    val nombreProducto: String,
    val marca: String,
    val proveedor: String,
    val envase: String,
    val cantidad: Int,
    val precioUnitario: BigDecimal,
    val createdAt: LocalDateTime?
)