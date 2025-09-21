package com.example.tinten.producto.dto

import java.math.BigDecimal

data class ProductoOrderTotalsDto(
    val totalPrecio: BigDecimal,   // SUM(precio_unitario * cantidad)
    val totalCantidad: BigDecimal, // SUM(cantidad)
    val totalProducto: BigDecimal  // COUNT(DISTINCT producto_id) como BigDecimal
)