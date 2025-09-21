package com.example.tinten.producto.repository


import com.example.tinten.producto.dto.ProductoOrderTotalsDto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import org.springframework.data.jpa.domain.Specification

interface ProductoOrdenCompraRepositoryCustom {
    fun sumTotals(spec: Specification<ProductoOrdenCompra>?): ProductoOrderTotalsDto
}
