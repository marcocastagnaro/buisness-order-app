package com.example.tinten.producto.entity

import com.example.tinten.ordenCompra.entity.OrdenCompra
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
@Entity
@Table(name = "producto_orden_compra")
class ProductoOrdenCompra(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    val producto: Producto,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    val ordenCompra: OrdenCompra,

    @Column(name = "precio_unitario", nullable = false, precision = 19, scale = 4)
    val precioUnitario: BigDecimal,

    @Column(nullable = false)
    val cantidad: Int,

    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: LocalDate? = null
)
