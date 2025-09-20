package com.example.tinten.producto.repository

import com.example.tinten.producto.dto.ProductCompraFilterDto
import com.example.tinten.producto.dto.ProductOrderDto
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
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

            if (f.createdFrom != null || f.createdTo != null) {
                val start = (f.createdFrom ?: LocalDate.MIN).atStartOfDay()
                val endExclusive = ((f.createdTo ?: LocalDate.MAX).plusDays(1)).atStartOfDay()
                predicates += cb.between(root.get<LocalDateTime>("createdAt"), start, endExclusive)
            }

            if (query?.orderList?.isEmpty() == true) {
                query.orderBy(cb.desc(root.get<LocalDateTime>("createdAt")))
            }

            cb.and(*predicates.toTypedArray())
        }
    }
}
