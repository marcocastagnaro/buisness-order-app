package com.example.tinten.producto.repository

import com.example.tinten.producto.entity.ProductoOrdenCompra
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProductoOrdenCompraRepository :
    JpaRepository<ProductoOrdenCompra, Long>,
    JpaSpecificationExecutor<ProductoOrdenCompra>