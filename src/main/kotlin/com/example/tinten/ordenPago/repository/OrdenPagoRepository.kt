package com.example.tinten.ordenPago.repository

import com.example.tinten.ordenPago.entity.OrdenPago
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface OrdenPagoRepository : JpaRepository<OrdenPago, Long>, JpaSpecificationExecutor<OrdenPago> {

    @Query("""
        SELECT o FROM OrdenPago o 
        WHERE LOWER(o.proveedor) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
           OR LOWER(o.razonSocial) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(o.concepto) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR o.cuit LIKE CONCAT('%', :searchTerm, '%')
           OR o.numeroFactura LIKE CONCAT('%', :searchTerm, '%')
    """)
    fun findBySearchTerm(@Param("searchTerm") searchTerm: String, pageable: Pageable): Page<OrdenPago>
}