package com.example.tinten.factura.controller

import com.example.tinten.factura.dto.FacturaDto
import com.example.tinten.factura.entity.FacturaEntity
import com.example.tinten.factura.repository.FacturaRepository
import com.example.tinten.factura.service.FacturaService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/facturas")
class FacturaController(
    private val facturaService: FacturaService,
) {

    @GetMapping
    fun getAll(): List<FacturaDto> {
        return facturaService.getAll()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): FacturaDto {
        return facturaService.getById(id)
    }


    @PostMapping
    fun create(@RequestBody dto: FacturaDto): FacturaDto {
        return facturaService.create(dto)
    }
}
