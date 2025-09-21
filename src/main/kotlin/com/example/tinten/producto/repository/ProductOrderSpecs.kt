package com.example.tinten.producto.repository

import com.example.tinten.producto.dto.ProductCompraFilterDto
import com.example.tinten.producto.dto.ProductOrderDto
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import jakarta.persistence.Tuple
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.LocalDateTime

object OrdenCompraItemSpecs {
    fun withFilter(f: ProductCompraFilterDto): Specification<ProductoOrdenCompra> {
        return Specification { root, query, cb ->
            val pJoin = root.join<Producto, ProductoOrdenCompra>("producto", JoinType.INNER)
            val predicates = mutableListOf<Predicate>()

            f.nombreProducto?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(cb.lower(pJoin.get("nombre")), "%${it.lowercase()}%")
            }
            f.marca?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(cb.lower(pJoin.get("marca")), "%${it.lowercase()}%")
            }
            f.proveedor?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(cb.lower(pJoin.get("proveedor")), "%${it.lowercase()}%")
            }
            f.envase?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(cb.lower(pJoin.get("envase")), "%${it.lowercase()}%")
            }

            val createdAtPath = root.get<LocalDateTime>("createdAt")

            f.createdFrom?.let { from ->
                predicates += cb.greaterThanOrEqualTo(createdAtPath, from.atStartOfDay())
            }

            f.createdTo?.let { to ->
                // endExclusive = inicio del día siguiente, salvo que sea 9999-12-31
                val endExclusive = if (to == LocalDate.of(9999, 12, 31)) {
                    // límite alto seguro sin overflow
                    LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000)
                } else {
                    to.plusDays(1).atStartOfDay()
                }
                predicates += cb.lessThan(createdAtPath, endExclusive)
            }

            val resultType = query?.resultType
            val isAggregate = resultType == java.lang.Long::class.java || resultType == Tuple::class.java
            if (query?.orderList?.isEmpty() == true && !isAggregate) {
                query.orderBy(cb.desc(root.get<LocalDateTime>("createdAt")))
            }

            cb.and(*predicates.toTypedArray())
        }
    }
}
