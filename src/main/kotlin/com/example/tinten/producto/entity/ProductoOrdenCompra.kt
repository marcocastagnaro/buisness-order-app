package com.example.tinten.producto.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import com.example.tinten.ordenCompra.entity.OrdenCompra
import java.math.BigDecimal

@Entity
@Table(name = "producto_orden_compra")
class ProductoOrdenCompra(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne @JoinColumn(name = "producto_id", nullable = false)
    val producto: Producto,

    @ManyToOne @JoinColumn(name = "orden_compra_id", nullable = false)
    val ordenCompra: OrdenCompra,

    @Column(nullable = false)
    val precioUnitario: BigDecimal,

    @Column(nullable = false)
    val cantidad: Int
)