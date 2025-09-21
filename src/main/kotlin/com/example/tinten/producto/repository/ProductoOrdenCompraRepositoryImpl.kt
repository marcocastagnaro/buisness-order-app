package com.example.tinten.producto.repository


import com.example.tinten.producto.dto.ProductoOrderTotalsDto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Tuple
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class ProductoOrdenCompraRepositoryImpl(
    @PersistenceContext private val em: EntityManager
) : ProductoOrdenCompraRepositoryCustom {

    override fun sumTotals(spec: Specification<ProductoOrdenCompra>?): ProductoOrderTotalsDto {
        val cb = em.criteriaBuilder
        val cq = cb.createTupleQuery()
        val root = cq.from(ProductoOrdenCompra::class.java)

        // Campos
        val precio = root.get<BigDecimal>("precioUnitario")
        val cantidadInt = root.get<Int>("cantidad")
        val cantidadBD = cb.toBigDecimal(cantidadInt) // para que SUM resulte BigDecimal

        // Importe = precio * cantidad
        val importeExpr = cb.prod(precio, cantidadBD)

        val sumImporte: jakarta.persistence.criteria.Expression<BigDecimal> =
            cb.coalesce(cb.sum(importeExpr), BigDecimal.ZERO)

        val sumCantidad: jakarta.persistence.criteria.Expression<BigDecimal> =
            cb.coalesce(cb.sum(cantidadBD), BigDecimal.ZERO)

        // Distintos productos
        val countDistinctProductos = cb.countDistinct(root.get<Any>("producto"))

        // Predicado desde la misma Specification
        val predicate = spec?.toPredicate(root, cq, cb)
        if (predicate != null) cq.where(predicate)

        cq.multiselect(
            sumImporte.alias("totalPrecio"),
            sumCantidad.alias("totalCantidad"),
            countDistinctProductos.alias("totalProductoCnt")
        )

        val t: Tuple = em.createQuery(cq).singleResult

        val totalPrecio = t.get("totalPrecio", BigDecimal::class.java) ?: BigDecimal.ZERO
        val totalCantidad = t.get("totalCantidad", BigDecimal::class.java) ?: BigDecimal.ZERO
        val totalProductoCnt = (t.get("totalProductoCnt", java.lang.Long::class.java) ?: 0L)

        return ProductoOrderTotalsDto(
            totalPrecio = totalPrecio,
            totalCantidad = totalCantidad,
            totalProducto = BigDecimal.valueOf(totalProductoCnt as Long)
        )
    }
}
