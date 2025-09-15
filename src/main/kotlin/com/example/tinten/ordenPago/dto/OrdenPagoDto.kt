package com.example.tinten.ordenPago.dto

import java.math.BigDecimal
import java.time.LocalDate

data class OrdenPagoDto(
    val id: Long? = null,
    val fechaCarga: LocalDate,
    val proveedor: String,
    val razonSocial: String,
    val cuit: String,
    val concepto: String,
    val fechaFactura: LocalDate,
    val numeroFactura: String,
    val importe: BigDecimal,
    val retenciones: BigDecimal? = null,
    val certificado: String? = null,
    val fechaCreacion: LocalDate? = null,
    val metodoPago: String? = null,
)

