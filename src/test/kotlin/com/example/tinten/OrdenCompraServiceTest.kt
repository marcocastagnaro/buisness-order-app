package com.example.tinten.ordenCompra.service

import com.example.tinten.ordenCompra.dto.*
import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.ordenCompra.repository.OrdenCompraRepository
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import com.example.tinten.producto.repository.ProductoRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class OrdenCompraServiceTest {

    @Mock
    private lateinit var ordenCompraRepository: OrdenCompraRepository

    @Mock
    private lateinit var productoRepository: ProductoRepository

    @InjectMocks
    private lateinit var ordenCompraService: OrdenCompraService

    private lateinit var testOrdenCompra: OrdenCompra
    private lateinit var testProducto: Producto

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        testProducto = Producto(id = 1L, nombre = "Producto1")
        testOrdenCompra = OrdenCompra(
            id = 1L,
            proveedorRazonSocial = "Proveedor1",
            atencionDe = "Contacto1",
            fecha = LocalDate.now(),
            moneda = "ARS",
            transporte = "Camion"
        ).apply {
            productos.add(
                ProductoOrdenCompra(
                    producto = testProducto,
                    ordenCompra = this,
                    precioUnitario = BigDecimal("100.00"),
                    cantidad = 2
                )
            )
        }
    }

    @Test
    fun test001_shouldReturnAllOrdenesDeCompra() {
        val page = PageImpl(listOf(testOrdenCompra))
        `when`(ordenCompraRepository.findAll(any(Pageable::class.java))).thenReturn(page)
        val result = ordenCompraService.findAll(Pageable.unpaged())
        assertTrue(result.size > 0)
        verify(ordenCompraRepository).findAll(any(Pageable::class.java))
    }

    @Test
    fun test002_shouldReturnOrdenDeCompraById() {
        `when`(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(testOrdenCompra))
        val result = ordenCompraService.findById(1L)
        assertEquals(testOrdenCompra.id, result.id)
        verify(ordenCompraRepository).findById(1L)
    }

    @Test
    fun test003_shouldCreateOrdenDeCompra() {
        val request = OrdenCompraCreateRequest(
            proveedorRazonSocial = "Proveedor Test",
            atencionDe = "Contacto Test",
            fecha = LocalDate.now(),
            moneda = "ARS",
            transporte = "Camion",
            productos = listOf(
                ProductoOrdenCompraCreateDto(
                    productoId = 0,
                    precioUnitario = BigDecimal("100.00"),
                    cantidad = 2,

                )
            )
        )
        `when`(productoRepository.findByNombre("Producto1")).thenReturn(Optional.of(testProducto))
        `when`(ordenCompraRepository.save(any(OrdenCompra::class.java))).thenReturn(testOrdenCompra)
        val created = ordenCompraService.create(request)
        assertEquals("Proveedor1", created.proveedorRazonSocial)
        verify(productoRepository).findByNombre("Producto1")
        verify(ordenCompraRepository).save(any(OrdenCompra::class.java))
    }

    @Test
    fun test004_shouldUpdateOrdenDeCompra() {
        `when`(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(testOrdenCompra))
        `when`(ordenCompraRepository.save(any(OrdenCompra::class.java))).thenReturn(testOrdenCompra)
        val updateRequest = OrdenCompraUpdateRequest(
            proveedorRazonSocial = "Proveedor Actualizado",
            atencionDe = null,
            fecha = null,
            moneda = null,
            transporte = null,
            productos = null
        )
        val updated = ordenCompraService.updateOrdenCompra(1L, updateRequest)
        assertEquals("Proveedor Actualizado", updated.proveedorRazonSocial)
        verify(ordenCompraRepository).findById(1L)
        verify(ordenCompraRepository).save(any(OrdenCompra::class.java))
    }

    @Test
    fun test005_shouldDeleteOrdenDeCompra() {
        `when`(ordenCompraRepository.existsById(1L)).thenReturn(true)
        doNothing().`when`(ordenCompraRepository).deleteById(1L)
        ordenCompraService.delete(1L)
        verify(ordenCompraRepository).existsById(1L)
        verify(ordenCompraRepository).deleteById(1L)
    }

    @Test
    fun test006_shouldThrowWhenOrdenDeCompraNotFound() {
        `when`(ordenCompraRepository.findById(99L)).thenReturn(Optional.empty())
        val ex = assertThrows<RuntimeException> { ordenCompraService.findById(99L) }
        assertTrue(ex.message!!.contains("no encontrada"))
        verify(ordenCompraRepository).findById(99L)
    }
}
