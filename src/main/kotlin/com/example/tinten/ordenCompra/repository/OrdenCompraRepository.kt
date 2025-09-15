package com.example.tinten.ordenCompra.repository

import com.example.tinten.ordenCompra.entity.OrdenCompra
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface OrdenCompraRepository : JpaRepository<OrdenCompra, Long>, JpaSpecificationExecutor<OrdenCompra> {

}