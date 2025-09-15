package com.example.tinten.producto.repository

import com.example.tinten.producto.entity.Producto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductoRepository : JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    fun findByNombre(productoNombre: String): Optional<Producto>
}