package com.example.tinten.ordenCompra.dto

import java.math.BigDecimal

data class ProductoOrdenCompraCreateDto(
    val productName: String,
    val precioUnitario: BigDecimal,
    val cantidad: Int
)