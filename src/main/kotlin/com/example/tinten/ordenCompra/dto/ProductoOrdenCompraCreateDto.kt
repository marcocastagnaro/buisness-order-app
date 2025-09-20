package com.example.tinten.ordenCompra.dto

import java.math.BigDecimal

data class ProductoOrdenCompraCreateDto(
    val productoId: Long,
    val precioUnitario: BigDecimal,
    val cantidad: Int
)