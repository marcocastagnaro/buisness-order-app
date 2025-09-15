package com.example.tinten

import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.repository.ProductoRepository
import com.example.tinten.producto.service.NotFoundException
import com.example.tinten.producto.service.ProductoService
import com.example.tinten.producto.service.ValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductoServiceTest {

    private lateinit var productoRepository: ProductoRepository
    private lateinit var service: ProductoService

    @BeforeEach
    fun setUp() {
        productoRepository = mock()
        service = ProductoService(productoRepository)
    }

    @Test
    fun test001_create_ok() {
        // Given
        val req = ProductoCreateRequest(
            nombre = " Mouse Gamer ",
            marca = " Logitech ",
            proveedor = " Tech SA ",
            envase = "1 kg"

        )

        // No existe producto con ese nombre
        whenever(productoRepository.findByNombre("Mouse Gamer"))
            .thenReturn(Optional.empty())

        // Al guardar, la DB devuelve la entidad con ID
        val savedEntity = Producto(
            id = 1L,
            nombre = "Mouse Gamer",
            marca = "Logitech",
            proveedor = "Tech SA"
        )
        whenever(productoRepository.save(any(Producto::class.java))).thenReturn(savedEntity)

        // When
        val dto = service.create(req)

        // Then
        assertEquals(1L, dto.id)
        assertEquals("Mouse Gamer", dto.nombre)
        assertEquals("Logitech", dto.marca)
        assertEquals("Tech SA", dto.proveedor)
        verify(productoRepository).findByNombre("Mouse Gamer")
        verify(productoRepository).save(any(Producto::class.java))
    }

    @Test
    fun test002_create_reject_blank_name() {
        // Given
        val req = ProductoCreateRequest(
            nombre = "   ",
            marca = "Logitech",
            proveedor = "Tech SA",
            envase = "1 kg"
        )

        // When / Then
        val ex = assertThrows<ValidationException> { service.create(req) }
        assertTrue(ex.message!!.contains("nombre", ignoreCase = true))
    }

    @Test
    fun test003_create_reject_duplicate_name() {
        // Given
        val req = ProductoCreateRequest(
            nombre = "Mouse Gamer",
            marca = "Logitech",
            proveedor = "Tech SA",
            envase = "1 kg"

        )

        val existing = Producto(
            id = 99L,
            nombre = "Mouse Gamer",
            marca = "Otra",
            proveedor = "Otro"
        )

        whenever(productoRepository.findByNombre("Mouse Gamer"))
            .thenReturn(Optional.of(existing))

        // When / Then
        val ex = assertThrows<ValidationException> { service.create(req) }
        assertTrue(ex.message!!.contains("Ya existe un producto", ignoreCase = true))
        verify(productoRepository).findByNombre("Mouse Gamer")
    }

    @Test
    fun test004_get_by_id_ok() {
        // Given
        val entity = Producto(
            id = 10L,
            nombre = "Teclado",
            marca = "KeyCorp",
            proveedor = "Dist SA"
        )
        whenever(productoRepository.findById(10L)).thenReturn(Optional.of(entity))

        // When
        val dto = service.getProductoById(10L)

        // Then
        assertEquals(10L, dto.id)
        assertEquals("Teclado", dto.nombre)
        assertEquals("KeyCorp", dto.marca)
        assertEquals("Dist SA", dto.proveedor)
        verify(productoRepository).findById(10L)
    }

    @Test
    fun test005_get_by_id_not_found() {
        // Given
        whenever(productoRepository.findById(777L)).thenReturn(Optional.empty())

        // When / Then
        assertThrows<NotFoundException> { service.getProductoById(777L) }
        verify(productoRepository).findById(777L)
    }

    @Test
    fun test006_get_all_without_page_ok() {
        // Given
        val e1 = Producto(id = 1L, nombre = "Mouse", marca = "Logitech", proveedor = "Tech SA")
        val e2 = Producto(id = 2L, nombre = "Teclado", marca = "KeyCorp", proveedor = "Dist SA")
        whenever(productoRepository.findAll()).thenReturn(listOf(e1, e2))

        // When
        val list = service.getAllProductosWithoutPage()

        // Then
        assertEquals(2, list.size)
        assertTrue(list.any { it.nombre == "Mouse" })
        assertTrue(list.any { it.nombre == "Teclado" })
        verify(productoRepository).findAll()
    }
}
