package com.example.tinten.factura.service

import com.example.tinten.ordenCompra.repository.OrdenCompraRepository
import com.example.tinten.ordenPago.repository.OrdenPagoRepository

import com.example.tinten.factura.dto.FacturaDto
import com.example.tinten.factura.entity.FacturaEntity
import com.example.tinten.factura.repository.FacturaRepository
import org.springframework.stereotype.Service

@Service
class FacturaService(
    private val facturaRepository: FacturaRepository,
    private val ordenCompraRepository: OrdenCompraRepository,
    private val ordenPagoRepository: OrdenPagoRepository
) {

    fun getAll(): List<FacturaDto> =
        facturaRepository.findAll().map { it.toDto() }

    fun getById(id: Long): FacturaDto =
        facturaRepository.findById(id).orElseThrow().toDto()

    fun create(dto: FacturaDto): FacturaDto {
        val ordenesCompra = ordenCompraRepository.findAllById(dto.ordenesCompraIds)
            .toSet()
        val ordenPago = dto.ordenPagoId?.let { ordenPagoRepository.findById(it).orElse(null) }
        val entity = FacturaEntity.fromDto(dto, ordenesCompra, ordenPago)
        return facturaRepository.save(entity).toDto()
    }
}
