package com.example.tinten.factura.repository

import com.example.tinten.factura.entity.FacturaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FacturaRepository : JpaRepository<FacturaEntity, Long>
