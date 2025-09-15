package com.example.tinten.ordenPago.entity

import com.example.tinten.ordenPago.dto.OrdenPagoDto
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "ordenes_pago")
class OrdenPago(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "fecha_carga", nullable = false)
    var fechaCarga: LocalDate = LocalDate.now(),

    @Column(name = "proveedor", nullable = false)
    var proveedor: String = "",

    @Column(name = "razon_social", nullable = false)
    var razonSocial: String = "",

    @Column(name = "cuit", nullable = false)
    var cuit: String = "",

    @Column(name = "concepto", nullable = false)
    var concepto: String = "",

    @Column(name = "fecha_factura", nullable = false)
    var fechaFactura: LocalDate = LocalDate.now(),

    @Column(name = "numero_factura", nullable = false)
    var numeroFactura: String = "",

    @Column(name = "importe", nullable = false, precision = 19, scale = 2)
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "retenciones", precision = 19, scale = 2)
    var retenciones: BigDecimal? = null,

    @Column(name = "certificado")
    var certificado: String? = null,

    @Column(name = "fecha_creacion")
    val fechaCreacion: LocalDate = LocalDate.now(),

    @Column(name="metodo_pago")
    var metodoPago: String = "",
) {
    companion object {
        fun toDto(ordenPago: OrdenPago): OrdenPagoDto {
            return OrdenPagoDto(
                id = ordenPago.id,
                fechaCarga = ordenPago.fechaCarga,
                proveedor = ordenPago.proveedor,
                razonSocial = ordenPago.razonSocial,
                cuit = ordenPago.cuit,
                concepto = ordenPago.concepto,
                fechaFactura = ordenPago.fechaFactura,
                numeroFactura = ordenPago.numeroFactura,
                importe = ordenPago.importe,
                retenciones = ordenPago.retenciones,
                certificado = ordenPago.certificado,
                fechaCreacion = ordenPago.fechaCreacion,
                metodoPago = ordenPago.metodoPago,
            )
        }
    }
}