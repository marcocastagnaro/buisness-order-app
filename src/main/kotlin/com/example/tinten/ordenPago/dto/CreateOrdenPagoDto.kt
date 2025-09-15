package com.example.tinten.ordenPago.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CreateOrdenPagoDto(
    val fechaCarga: LocalDate,
    val proveedor: String,
    val razonSocial: String,
    val cuit: String,
    val concepto: String,
    val fechaFactura: LocalDate,
    val numeroFactura: String,
    val importe: BigDecimal,
    val metodoPago: String,
    val retenciones: BigDecimal? = null,
    val certificado: String? = null,
)
