package com.example.tinten.ordenCompra.dto

import java.math.BigDecimal

data class ProductoOrdenCompraDto(
    val id: Long?,
    val productoId: Long,
    val productoNombre: String,
    val productoMarca: String,
    val productoProveedor: String,
    val precioUnitario: BigDecimal,
    val cantidad: Int
)