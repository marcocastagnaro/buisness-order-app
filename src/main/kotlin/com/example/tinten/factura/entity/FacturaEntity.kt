package com.example.tinten.factura.entity

import com.example.tinten.factura.dto.FacturaDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.ordenPago.entity.OrdenPago
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import java.time.LocalDateTime
@Entity
@Table(name = "facturas")
class FacturaEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name= "factura_img", nullable = false)
    val url: String? = "",

    @ManyToMany
    @JoinTable(
        name = "factura_orden_compra",
        joinColumns = [JoinColumn(name = "factura_id")],
        inverseJoinColumns = [JoinColumn(name = "orden_compra_id")]
    )
    var ordenesCompra: MutableSet<OrdenCompra> = mutableSetOf(),

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "orden_pago_id", unique = true)
    var ordenPago: OrdenPago? = null,


    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
) {
    companion object {
        fun fromDto(dto: FacturaDto, ordenesCompra: Set<OrdenCompra>, ordenPago: OrdenPago?): FacturaEntity {
            return FacturaEntity(
                id = dto.id,
                url = dto.url,
                ordenesCompra = ordenesCompra.toMutableSet(),
                ordenPago = ordenPago,
                createdAt = dto.createdAt
            )
        }
    }

    fun toDto(): FacturaDto {
        return FacturaDto(
            id = this.id,
            url = this.url,
            ordenesCompraIds = this.ordenesCompra.mapNotNull { it.id }.toSet(),
            ordenPagoId = this.ordenPago?.id,
            createdAt = this.createdAt
        )
    }

}