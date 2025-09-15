package com.example.tinten.ordenCompra.entity

import com.example.tinten.ordenCompra.dto.OrdenCompraDto
import com.example.tinten.ordenCompra.dto.ProductoOrdenCompraDto
import com.example.tinten.producto.entity.Producto
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import com.example.tinten.producto.entity.ProductoOrdenCompra

@Entity
@Table(name = "ordenes_compra")
class OrdenCompra(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "proveedor_razon_social", nullable = false)
    var proveedorRazonSocial: String = "",

    @Column(name = "atencion_de", nullable = false)
    var atencionDe: String = "",

    @Column(name = "fecha", nullable = false)
    var fecha: LocalDate = LocalDate.now(),

    @Column(name = "moneda", nullable = false)
    var moneda: String = "",

    @OneToMany(mappedBy = "ordenCompra", cascade = [CascadeType.ALL], orphanRemoval = true)
    val productos: MutableList<ProductoOrdenCompra> = mutableListOf(),

    @Column(name = "transporte")
    var transporte: String? = null,

    @Column(name = "fecha_creacion")
    var fechaCreacion: LocalDate = LocalDate.now(),

    @Column (name="domicilio")
    var domicilio: String = "",

){
    fun toDto(): OrdenCompraDto {
        return OrdenCompraDto(
            id = this.id,
            proveedorRazonSocial = this.proveedorRazonSocial,
            atencionDe = this.atencionDe,
            fecha = this.fecha,
            moneda = this.moneda,
            productos = this.productos.map { productoOrdenCompra ->
                ProductoOrdenCompraDto(
                    id = productoOrdenCompra.id,
                    productoId = productoOrdenCompra.producto.id!!,
                    productoNombre = productoOrdenCompra.producto.nombre,
                    productoMarca = productoOrdenCompra.producto.marca,
                    productoProveedor = productoOrdenCompra.producto.proveedor,
                    precioUnitario = productoOrdenCompra.precioUnitario,
                    cantidad = productoOrdenCompra.cantidad
                )
            },
            transporte = this.transporte,
            fechaCreacion = this.fechaCreacion,
            domicilio = this.domicilio
        )
    }

}