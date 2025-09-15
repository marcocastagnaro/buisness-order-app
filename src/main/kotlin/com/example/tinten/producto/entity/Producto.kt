package com.example.tinten.producto.entity

import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductoDto
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "productos")
class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "nombre", nullable = false)
    var nombre: String = "",

    @Column(name = "marca", nullable = false)
    var marca: String = "",

    @Column(name = "proveedor", nullable = false)
    var proveedor: String = "",

    @OneToMany(mappedBy = "producto", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ordenesCompra: MutableList<ProductoOrdenCompra> = mutableListOf(),

    @Column (name="envase", nullable = false)
    var envase: String = "",

    @Column (name= "createdAt", nullable = false)
    var createdAt: LocalDate = LocalDate.now(),
) {

    companion object {
        fun toDto(producto: Producto): ProductoDto {
            return ProductoDto(
                id = producto.id,
                nombre = producto.nombre,
                marca = producto.marca,
                proveedor = producto.proveedor,
                envase = producto.envase,
                createdAt = producto.createdAt,
            )
        }
        fun fromDto(dto: ProductoCreateRequest): Producto {
            return Producto(
                nombre = dto.nombre,
                marca = dto.marca,
                proveedor = dto.proveedor,
                envase = dto.envase
            )
        }
    }
}