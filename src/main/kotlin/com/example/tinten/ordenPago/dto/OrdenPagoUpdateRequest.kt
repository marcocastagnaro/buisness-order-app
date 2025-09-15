package com.example.tinten.ordenPago.dto

import java.math.BigDecimal
import java.time.LocalDate

data class OrdenPagoUpdateRequest(
    val fechaCarga: LocalDate? = null,
    val proveedor: String? = null,
    val razonSocial: String? = null,
    val cuit: String? = null,
    val concepto: String? = null,
    val fechaFactura: LocalDate? = null,
    val numeroFactura: String? = null,
    val importe: BigDecimal? = null,
    val retenciones: BigDecimal? = null,
    val certificado: String? = null,
    val metodoPago: String? = null,
)