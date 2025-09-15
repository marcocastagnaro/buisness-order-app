package com.example.tinten.ordenPago.service

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.ordenPago.dto.*
import com.example.tinten.ordenPago.entity.OrdenPago
import com.example.tinten.ordenPago.repository.OrdenPagoRepository
import jakarta.validation.ValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
@Transactional
class OrdenPagoService(
    private val ordenPagoRepository: OrdenPagoRepository,
) {

    fun findAll(pageable: Pageable): Page<OrdenPagoDto> {
        return ordenPagoRepository.findAll(pageable).map { OrdenPago.toDto(it) }
    }

    fun findById(id: Long): OrdenPagoDto {
        val ordenPago = ordenPagoRepository.findById(id)
            .orElseThrow { NotFoundException("Orden de pago no encontrada con id: $id") }
        return OrdenPago.toDto(ordenPago)
    }

    fun create(request: CreateOrdenPagoDto): OrdenPagoDto {
        validarCreate(request)

        val ordenPago = OrdenPago().apply {
            fechaCarga = request.fechaCarga
            proveedor = request.proveedor.trim()
            razonSocial = request.razonSocial.trim()
            cuit = request.cuit.trim()
            concepto = request.concepto.trim()
            fechaFactura = request.fechaFactura
            numeroFactura = request.numeroFactura.trim()
            importe = request.importe.setScale(2, RoundingMode.HALF_UP)
            retenciones = request.retenciones?.setScale(2, RoundingMode.HALF_UP)
            certificado = request.certificado?.trim()
        }

        val saved = ordenPagoRepository.save(ordenPago)
        return OrdenPago.toDto(saved)
    }

    fun update(id: Long, request: OrdenPagoUpdateRequest): OrdenPagoDto {
        val ordenPago = ordenPagoRepository.findById(id)
            .orElseThrow { NotFoundException("Orden de pago no encontrada con id: $id") }

        // Construimos una "vista efectiva" para validar reglas cruzadas
        val efectivaFechaCarga = request.fechaCarga ?: ordenPago.fechaCarga
        val efectivaFechaFactura = request.fechaFactura ?: ordenPago.fechaFactura
        val efectivaImporte = request.importe ?: ordenPago.importe
        val efectivaRetenciones = request.retenciones ?: ordenPago.retenciones
        val efectivaCertificado = (request.certificado ?: ordenPago.certificado)?.trim()

        validarUpdateParcial(
            fechaCarga = efectivaFechaCarga,
            fechaFactura = efectivaFechaFactura,
            proveedor = request.proveedor ?: ordenPago.proveedor,
            razonSocial = request.razonSocial ?: ordenPago.razonSocial,
            cuit = request.cuit ?: ordenPago.cuit,
            numeroFactura = request.numeroFactura ?: ordenPago.numeroFactura,
            importe = efectivaImporte,
            retenciones = efectivaRetenciones,
            certificado = efectivaCertificado
        )

        request.fechaCarga?.let { ordenPago.fechaCarga = it }
        request.proveedor?.let { ordenPago.proveedor = it.trim() }
        request.razonSocial?.let { ordenPago.razonSocial = it.trim() }
        request.cuit?.let { ordenPago.cuit = it.trim() }
        request.concepto?.let { ordenPago.concepto = it.trim() }
        request.fechaFactura?.let { ordenPago.fechaFactura = it }
        request.numeroFactura?.let { ordenPago.numeroFactura = it.trim() }
        request.importe?.let { ordenPago.importe = it.setScale(2, RoundingMode.HALF_UP) }
        request.retenciones?.let { ordenPago.retenciones = it.setScale(2, RoundingMode.HALF_UP) }
        request.certificado?.let { ordenPago.certificado = it.trim() }

        val updated = ordenPagoRepository.save(ordenPago)
        return OrdenPago.toDto(updated)
    }

    fun delete(id: Long) {
        if (!ordenPagoRepository.existsById(id)) {
            throw NotFoundException("Orden de pago no encontrada con id: $id")
        }
        ordenPagoRepository.deleteById(id)
    }

    private fun validarCreate(req: CreateOrdenPagoDto) {
        // Textos requeridos
        if (req.proveedor.isNullOrBlank()) throw ValidationException("El proveedor es obligatorio")
        if (req.razonSocial.isNullOrBlank()) throw ValidationException("La razón social es obligatoria")
        if (!esCuitValido(req.cuit)) throw ValidationException("CUIT inválido")

        // Fechas
        validarFechas(req.fechaCarga, req.fechaFactura)

        // Montos
        validarMontos(req.importe, req.retenciones)

        // Factura (opcional pero si viene debe validar)
        req.numeroFactura.let {
            if (!esNumeroFacturaValido(it)) throw ValidationException("Número de factura con formato inválido")
        }

        // Consistencia documental
        if (req.retenciones ?: BigDecimal.ZERO > BigDecimal.ZERO && req.certificado.isNullOrBlank()) {
            throw ValidationException("Si hay retenciones > 0, el certificado es obligatorio")
        }
    }
    private fun validarUpdateParcial(
        fechaCarga: LocalDate?,
        fechaFactura: LocalDate?,
        proveedor: String?,
        razonSocial: String?,
        cuit: String?,
        numeroFactura: String?,
        importe: BigDecimal?,
        retenciones: BigDecimal?,
        certificado: String?
    ) {
        proveedor?.let { if (it.isBlank()) throw ValidationException("El proveedor no puede ser vacío") }
        razonSocial?.let { if (it.isBlank()) throw ValidationException("La razón social no puede ser vacía") }
        cuit?.let { if (!esCuitValido(it)) throw ValidationException("CUIT inválido") }

        if (fechaCarga != null || fechaFactura != null) {
            validarFechas(
                fechaCarga ?: LocalDate.now(), // valor de respaldo si estuviera nulo (no debería)
                fechaFactura
            )
        }

        if (importe != null || retenciones != null) {
            val imp = importe ?: BigDecimal.ONE // respaldo > 0
            val ret = retenciones ?: BigDecimal.ZERO
            validarMontos(imp, ret)
        }

        numeroFactura?.let {
            if (!esNumeroFacturaValido(it)) throw ValidationException("Número de factura con formato inválido")
        }

        if (retenciones != null && importe != null) {
            if (retenciones > BigDecimal.ZERO && (certificado == null || certificado.isBlank())) {
                throw ValidationException("Si hay retenciones > 0, el certificado es obligatorio")
            }
        }
    }

    private fun validarFechas(fechaCarga: LocalDate, fechaFactura: LocalDate?) {
        val hoy = LocalDate.now()
        if (fechaCarga.isAfter(hoy)) throw ValidationException("La fecha de carga no puede ser futura")
        if (fechaFactura != null) {
            if (fechaFactura.isAfter(hoy)) throw ValidationException("La fecha de factura no puede ser futura")
            if (fechaFactura.isAfter(fechaCarga)) {
                throw ValidationException("La fecha de factura no puede ser posterior a la fecha de carga")
            }
        }
    }

    private fun validarMontos(importe: BigDecimal, retenciones: BigDecimal?) {
        if (importe <= BigDecimal.ZERO) throw ValidationException("El importe debe ser > 0")
        if (retenciones != null) {
            if (retenciones < BigDecimal.ZERO) throw ValidationException("Las retenciones no pueden ser negativas")
        }
        if (retenciones != null) {
            if (retenciones > importe) throw ValidationException("Las retenciones no pueden superar el importe")
        }
    }

    private fun esCuitValido(cuit: String): Boolean {
        val digits = cuit.filter { it.isDigit() }

        // Validar que tenga 11 dígitos
        if (digits.length != 11) return false

        val cuitArray = digits.map { it.digitToInt() }

        // Pesos del algoritmo de AFIP
        val pesos = listOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)

        // Calcular suma ponderada
        val suma = pesos.zip(cuitArray).sumOf { (peso, digito) -> peso * digito }

        // Obtener dígito verificador esperado
        val resto = suma % 11
        val dvCalculado = when (val resultado = 11 - resto) {
            11 -> 0
            10 -> 9
            else -> resultado
        }

        val dvReal = cuitArray[10]

        return dvCalculado == dvReal
    }
    // Acepta: "A 0001-00000001", "B 0002-00012345", "0001-00000001"
    private fun esNumeroFacturaValido(nro: String): Boolean {
        val trimmed = nro.trim().uppercase()
        val regex = Regex("""^[A-Z]\s?\d{4}-\d{8}$|^\d{4}-\d{8}$""")
        return regex.matches(trimmed)
    }

} 