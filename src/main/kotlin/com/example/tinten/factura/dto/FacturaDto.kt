package com.example.tinten.factura.dto

import java.time.LocalDateTime

data class FacturaDto(
    val id: Long?,
    val url: String?,
    val ordenesCompraIds: Set<Long>,
    val ordenPagoId: Long?,
    val createdAt: LocalDateTime?
)