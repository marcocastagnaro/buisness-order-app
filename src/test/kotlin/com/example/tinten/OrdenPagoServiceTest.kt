package com.example.tinten.ordenPago.service

import com.example.tinten.ordenPago.dto.CreateOrdenPagoDto
import com.example.tinten.ordenPago.entity.OrdenPago
import com.example.tinten.ordenPago.repository.OrdenPagoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class OrdenPagoServiceTest {

    @Mock
    private lateinit var ordenPagoRepository: OrdenPagoRepository

    @InjectMocks
    private lateinit var ordenPagoService: OrdenPagoService

    private lateinit var testOrdenPago: OrdenPago
    private lateinit var createRequest: CreateOrdenPagoDto

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        testOrdenPago = OrdenPago().apply {
            fechaCarga = LocalDate.of(2025, 9, 8)
            proveedor = "Test Proveedor"
            razonSocial = "Test Proveedor S.A."
            cuit = "30-71750790-4"
            concepto = "Servicios de prueba"
            fechaFactura = LocalDate.of(2025, 9, 5)
            numeroFactura = "0001-00001234"
            importe = BigDecimal("1000.00")
            retenciones = BigDecimal("50.00")
            certificado = "CERT-001"
            metodoPago="Cheque"
        }

        createRequest = CreateOrdenPagoDto(
            fechaCarga = LocalDate.of(2025, 9, 8),
            proveedor = "Test Proveedor",
            razonSocial = "Test Proveedor S.A.",
            cuit = "30-71750790-4",
            concepto = "Servicios de prueba",
            fechaFactura = LocalDate.of(2025, 9, 5),
            numeroFactura = "0001-00001234",
            importe = BigDecimal("1000.00"),
            retenciones = BigDecimal("50.00"),
            certificado = "CERT-001",
            metodoPago = "Cheque",
        )
    }

    @Test
    fun `test001_createOrdenPago`() {
        // Given
        val savedOrdenPago = OrdenPago(
            proveedor = testOrdenPago.proveedor,
            razonSocial = testOrdenPago.razonSocial,
            cuit = testOrdenPago.cuit,
            concepto = testOrdenPago.concepto,
            importe = testOrdenPago.importe,
            retenciones = testOrdenPago.retenciones,
            certificado = testOrdenPago.certificado,
            metodoPago = testOrdenPago.metodoPago,
        ).apply {
            id = 1L
        }
        `when`(ordenPagoRepository.save(any(OrdenPago::class.java))).thenReturn(savedOrdenPago)

        // When
        val result = ordenPagoService.create(createRequest)

        // Then
        assertNotNull(result)
        assertEquals(testOrdenPago.proveedor, result.proveedor)
        assertEquals(testOrdenPago.razonSocial, result.razonSocial)
        assertEquals(testOrdenPago.cuit, result.cuit)
        assertEquals(testOrdenPago.concepto, result.concepto)
        assertEquals(testOrdenPago.importe, result.importe)
        assertEquals(testOrdenPago.retenciones, result.retenciones)
        assertEquals(testOrdenPago.certificado, result.certificado)
        assertEquals(testOrdenPago.metodoPago, result.metodoPago)

        verify(ordenPagoRepository, times(1)).save(any(OrdenPago::class.java))
    }

    @Test
    fun `test002_findOrdenPagoById`() {
        // Given
        val ordenPagoId = 1L
        val ordenPagoWithId = OrdenPago(
            proveedor = testOrdenPago.proveedor,
            razonSocial = testOrdenPago.razonSocial,
            cuit = testOrdenPago.cuit,
            concepto = testOrdenPago.concepto,
            importe = testOrdenPago.importe,
            retenciones = testOrdenPago.retenciones,
            certificado = testOrdenPago.certificado,
            id = 1L
        )
        `when`(ordenPagoRepository.findById(ordenPagoId)).thenReturn(Optional.of(ordenPagoWithId))

        // When
        val result = ordenPagoService.findById(ordenPagoId)

        // Then
        assertNotNull(result)
        assertEquals(testOrdenPago.proveedor, result.proveedor)
        assertEquals(testOrdenPago.razonSocial, result.razonSocial)
        assertEquals(testOrdenPago.cuit, result.cuit)

        verify(ordenPagoRepository, times(1)).findById(ordenPagoId)
    }

    @Test
    fun `test003_exceptionOrdenDePagoNotfOUND`() {
        // Given
        val ordenPagoId = 999L
        `when`(ordenPagoRepository.findById(ordenPagoId)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<RuntimeException> {
            ordenPagoService.findById(ordenPagoId)
        }

        assertEquals("Orden de pago no encontrada con id: $ordenPagoId", exception.message)
        verify(ordenPagoRepository, times(1)).findById(ordenPagoId)
    }

    @Test
    fun `test004_findAllByPagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val ordenesPago = listOf(testOrdenPago)
        val page = PageImpl(ordenesPago, pageable, ordenesPago.size.toLong())

        `when`(ordenPagoRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = ordenPagoService.findAll(pageable)

        // Then
        assertNotNull(result)
        assertEquals(1, result.totalElements)
        assertEquals(1, result.content.size)
        assertEquals(testOrdenPago.proveedor, result.content[0].proveedor)

        verify(ordenPagoRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `test005_deleteOrdenDePago`() {
        // Given
        val ordenPagoId = 1L
        `when`(ordenPagoRepository.existsById(ordenPagoId)).thenReturn(true)
        doNothing().`when`(ordenPagoRepository).deleteById(ordenPagoId)

        // When
        ordenPagoService.delete(ordenPagoId)

        // Then
        verify(ordenPagoRepository, times(1)).existsById(ordenPagoId)
        verify(ordenPagoRepository, times(1)).deleteById(ordenPagoId)
    }

    @Test
    fun `test006_exceptionWhenDeletingOrdenNotFound`() {
        // Given
        val ordenPagoId = 999L
        `when`(ordenPagoRepository.existsById(ordenPagoId)).thenReturn(false)

        // When & Then
        val exception = assertThrows<RuntimeException> {
            ordenPagoService.delete(ordenPagoId)
        }

        assertEquals("Orden de pago no encontrada con id: $ordenPagoId", exception.message)
        verify(ordenPagoRepository, times(1)).existsById(ordenPagoId)
        verify(ordenPagoRepository, never()).deleteById(ordenPagoId)
    }

    @Test
    fun `test007_createOrdenPagoMinimalData`() {
        // Given
        val minimalRequest = CreateOrdenPagoDto(
            fechaCarga = LocalDate.now(),
            proveedor = "Minimal Provider",
            razonSocial = "Minimal Provider S.R.L.",
            cuit = "20-87654321-5",
            concepto = "Minimal service",
            fechaFactura = LocalDate.now(),
            numeroFactura = "B0001-00000001",
            importe = BigDecimal("500.00"),
            metodoPago = "Cheque"
        )

        val savedEntity = OrdenPago().apply {
            fechaCarga = minimalRequest.fechaCarga
            proveedor = minimalRequest.proveedor
            razonSocial = minimalRequest.razonSocial
            cuit = minimalRequest.cuit
            concepto = minimalRequest.concepto
            fechaFactura = minimalRequest.fechaFactura
            numeroFactura = minimalRequest.numeroFactura
            importe = minimalRequest.importe
            metodoPago = minimalRequest.metodoPago
        }

        `when`(ordenPagoRepository.save(any(OrdenPago::class.java))).thenReturn(savedEntity)

        // When
        val result = ordenPagoService.create(minimalRequest)

        // Then
        assertNotNull(result)
        assertEquals(minimalRequest.proveedor, result.proveedor)
        assertEquals(minimalRequest.importe, result.importe)
        assertNull(result.retenciones)
        assertNull(result.certificado)

        verify(ordenPagoRepository, times(1)).save(any(OrdenPago::class.java))
    }
}