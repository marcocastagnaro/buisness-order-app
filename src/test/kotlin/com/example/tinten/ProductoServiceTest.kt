package com.example.tinten

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.exception.domainExceptions.ValidationExceptions
import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.producto.dto.ProductoCreateRequest
import com.example.tinten.producto.dto.ProductCompraFilterDto
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import com.example.tinten.producto.repository.ProductoOrdenCompraRepository
import com.example.tinten.producto.repository.ProductoRepository
import com.example.tinten.producto.service.ProductoService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ProductoServiceTest {

    private lateinit var productoRepository: ProductoRepository
    private lateinit var productoOrdenCompraRepository: ProductoOrdenCompraRepository
    private lateinit var service: ProductoService

    // Datos comunes
    private lateinit var productoMouse: Producto
    private lateinit var productoTeclado: Producto
    private lateinit var productoMonitor: Producto

    // Stub simple para OrdenCompra (si tu clase es final, necesitás mockito-inline)
    private interface OrdenCompraStub { val id: Long? }

    @BeforeEach
    fun setUp() {
        productoRepository = mock()
        productoOrdenCompraRepository = mock()
        service = ProductoService(productoRepository, productoOrdenCompraRepository)

        productoMouse = Producto(
            id = 1L, nombre = "Mouse", marca = "Logitech", proveedor = "Tech SA", envase = "1 kg"
        )
        productoTeclado = Producto(
            id = 2L, nombre = "Teclado", marca = "KeyCorp", proveedor = "Dist SA", envase = "caja"
        )
        productoMonitor = Producto(
            id = 3L, nombre = "Monitor", marca = "Samsung", proveedor = "Display Inc", envase = "caja"
        )
    }

    // -------------------
    // CREATE
    // -------------------

    @Test
    fun test001_create_ok() {
        val req = ProductoCreateRequest(
            nombre = " Mouse Gamer ",
            marca = " Logitech ",
            proveedor = " Tech SA ",
            envase = "1 kg"
        )

        whenever(productoRepository.findByNombre("Mouse Gamer"))
            .thenReturn(Optional.empty())

        val savedEntity = Producto(
            id = 1L,
            nombre = "Mouse Gamer",
            marca = "Logitech",
            proveedor = "Tech SA",
            envase = "1 kg"
        )

// Opción A (explícita con tipo)
        whenever(productoRepository.save(any<Producto>())).thenReturn(savedEntity)

        val dto = service.create(req)

        assertEquals(1L, dto.id)
        assertEquals("Mouse Gamer", dto.nombre)
        assertEquals("Logitech", dto.marca)
        assertEquals("Tech SA", dto.proveedor)
        assertEquals("1 kg", dto.envase)
        verify(productoRepository).findByNombre("Mouse Gamer")
        verify(productoRepository).save(
            org.mockito.kotlin.check {
                assertEquals("Mouse Gamer", it.nombre)
                assertEquals("Logitech", it.marca)
                assertEquals("Tech SA", it.proveedor)
                assertEquals("1 kg", it.envase)
            }
        )    }

    @Test
    fun test002_create_reject_blank_name() {
        val req = ProductoCreateRequest(
            nombre = "   ",
            marca = "Logitech",
            proveedor = "Tech SA",
            envase = "1 kg"
        )
        val ex = assertThrows<ValidationExceptions> { service.create(req) }
        assertTrue(ex.message!!.contains("nombre", ignoreCase = true))
    }

    @Test
    fun test003_create_reject_duplicate_name_by_findByNombre() {
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
            proveedor = "Otro",
            envase = "caja"
        )
        whenever(productoRepository.findByNombre("Mouse Gamer"))
            .thenReturn(Optional.of(existing))

        val ex = assertThrows<ValidationExceptions> { service.create(req) }
        assertTrue(ex.message!!.contains("Ya existe un producto", ignoreCase = true))
        verify(productoRepository).findByNombre("Mouse Gamer")
    }

    @Test
    fun test003b_create_reject_duplicate_name_ignore_case_via_findAll() {
        val req = ProductoCreateRequest(
            nombre = "mouse",
            marca = "Logitech",
            proveedor = "Tech SA",
            envase = "1 kg"
        )
        // Simulamos que findByNombre no lo encuentra exacto, pero en findAll sí existe "Mouse"
        whenever(productoRepository.findByNombre("mouse"))
            .thenReturn(Optional.empty())
        whenever(productoRepository.findAll())
            .thenReturn(listOf(productoMouse))

        val ex = assertThrows<ValidationExceptions> { service.create(req) }
        assertTrue(ex.message!!.contains("Ya existe un producto", ignoreCase = true))
        verify(productoRepository).findAll()
    }

    @Test
    fun test003c_create_reject_invalid_lengths_and_chars() {
        val tooLong120 = "a".repeat(121)
        val tooLong80 = "b".repeat(81)
        val badChars = "Nombre@@@###"

        // nombre > 120
        assertThrows<ValidationExceptions> {
            service.create(ProductoCreateRequest(tooLong120, "Marca", "Prov", "envase"))
        }
        // marca > 80
        assertThrows<ValidationExceptions> {
            service.create(ProductoCreateRequest("Nombre", tooLong80, "Prov", "envase"))
        }
        // proveedor > 120
        assertThrows<ValidationExceptions> {
            service.create(ProductoCreateRequest("Nombre", "Marca", tooLong120, "envase"))
        }
        // caracteres inválidos
        assertThrows<ValidationExceptions> {
            service.create(ProductoCreateRequest(badChars, "Marca", "Prov", "envase"))
        }
    }

    // -------------------
    // GET BY ID / LIST
    // -------------------

    @Test
    fun test004_get_by_id_ok() {
        whenever(productoRepository.findById(10L)).thenReturn(Optional.of(
            Producto(id = 10L, nombre = "Teclado", marca = "KeyCorp", proveedor = "Dist SA", envase = "caja")
        ))
        val dto = service.getProductoById(10L)
        assertEquals(10L, dto.id)
        assertEquals("Teclado", dto.nombre)
        assertEquals("KeyCorp", dto.marca)
        assertEquals("Dist SA", dto.proveedor)
        verify(productoRepository).findById(10L)
    }

    @Test
    fun test005_get_by_id_not_found() {
        whenever(productoRepository.findById(777L)).thenReturn(Optional.empty())
        assertThrows<NotFoundException> { service.getProductoById(777L) }
        verify(productoRepository).findById(777L)
    }

    @Test
    fun test006_get_all_without_page_ok() {
        whenever(productoRepository.findAll()).thenReturn(listOf(productoMouse, productoTeclado))
        val list = service.getAllProductosWithoutPage()
        assertEquals(2, list.size)
        assertTrue(list.any { it.nombre == "Mouse" })
        assertTrue(list.any { it.nombre == "Teclado" })
        verify(productoRepository).findAll()
    }

    // -------------------
    // SEARCH (sobre ProductoOrdenCompra + join Producto)
    // -------------------


    @Test
    fun test007_search_by_nombreProducto_ok() {
        val pageable: Pageable = PageRequest.of(0, 10)

        val oc = mock<OrdenCompra>()

        val poc = ProductoOrdenCompra(
            id = 100L,
            producto = productoMouse,
            ordenCompra = oc,
            precioUnitario = BigDecimal("150.00"),
            cantidad = 5
        ).apply {
            createdAt = LocalDateTime.of(2025, 9, 15, 10, 0)
        }

        val page: Page<ProductoOrdenCompra> = PageImpl(listOf(poc))
        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>())).thenReturn(page)

        val filter = ProductCompraFilterDto(nombreProducto = "Mouse")
        val result = service.search(filter, pageable)

        assertEquals(1, result.totalElements)
        val row = result.content.first()
        assertEquals("Mouse", row.nombreProducto)
        assertEquals(BigDecimal("150.00"), row.precioUnitario)
        assertEquals(5, row.cantidad)
        assertEquals(1L, row.productoId)
    }

    @Test
    fun test008_search_by_marca_proveedor_envase_ok() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val oc = mock<OrdenCompra>()

        val poc = ProductoOrdenCompra(
            id = 101L,
            producto = productoTeclado, // marca=KeyCorp, proveedor=Dist SA, envase=caja
            ordenCompra = oc as Any as com.example.tinten.ordenCompra.entity.OrdenCompra,
            precioUnitario = BigDecimal("200.00"),
            cantidad = 2
        ).apply {
            createdAt = LocalDateTime.of(2025, 9, 10, 9, 0)
        }

        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>()))
            .thenReturn(PageImpl(listOf(poc)))

        val filter = ProductCompraFilterDto(
            marca = "key",           // like case-insensitive
            proveedor = "dist",
            envase = "caja"
        )
        val result = service.search(filter, pageable)

        assertEquals(1, result.totalElements)
        val row = result.content.first()
        assertEquals("Teclado", row.nombreProducto)
        assertEquals("KeyCorp", row.marca)
        assertEquals("Dist SA", row.proveedor)
        assertEquals("caja", row.envase)
    }

    @Test
    fun test09_search_by_fecha_rango_inclusive_ok() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val oc = mock<OrdenCompra>()

        val pocDentro = ProductoOrdenCompra(
            id = 103L,
            producto = productoMouse,
            ordenCompra = oc as Any as com.example.tinten.ordenCompra.entity.OrdenCompra,
            precioUnitario = BigDecimal("100.00"),
            cantidad = 3
        ).apply { createdAt = LocalDateTime.of(2025, 9, 12, 12, 0) }

        // Simulamos que el repo devuelve sólo lo que matchea el spec
        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>()))
            .thenReturn(PageImpl(listOf(pocDentro)))

        val filter = ProductCompraFilterDto(
            createdFrom = LocalDate.of(2025, 9, 10),
            createdTo = LocalDate.of(2025, 9, 12) // inclusive (hasta 23:59:59) por la lógica endExclusive
        )
        val result = service.search(filter, pageable)

        assertEquals(1, result.totalElements)
        assertEquals(LocalDate.of(2025, 9, 12), result.content.first().createdAt?.toLocalDate())
    }

    @Test
    fun test011_search_by_fecha_only_from_ok() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val oc = mock<OrdenCompra>()

        val poc = ProductoOrdenCompra(
            id = 104L,
            producto = productoMouse,
            ordenCompra = oc as Any as com.example.tinten.ordenCompra.entity.OrdenCompra,
            precioUnitario = BigDecimal("90.00"),
            cantidad = 1
        ).apply { createdAt = LocalDateTime.of(2025, 9, 20, 10, 0) }

        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>()))
            .thenReturn(PageImpl(listOf(poc)))

        val filter = ProductCompraFilterDto(createdFrom = LocalDate.of(2025, 9, 15))
        val result = service.search(filter, pageable)

        assertEquals(1, result.totalElements)
        assertTrue(result.content.first().createdAt!!.isAfter(LocalDateTime.of(2025, 9, 15, 0, 0)))
    }

    @Test
    fun test012_search_by_fecha_only_to_ok() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val oc = mock<OrdenCompra>()

        val poc = ProductoOrdenCompra(
            id = 105L,
            producto = productoMouse,
            ordenCompra = oc as Any as com.example.tinten.ordenCompra.entity.OrdenCompra,
            precioUnitario = BigDecimal("110.00"),
            cantidad = 2
        ).apply { createdAt = LocalDateTime.of(2025, 9, 1, 8, 0) }

        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>()))
            .thenReturn(PageImpl(listOf(poc)))

        val filter = ProductCompraFilterDto(createdTo = LocalDate.of(2025, 9, 5))
        val result = service.search(filter, pageable)

        assertEquals(1, result.totalElements)
        assertTrue(result.content.first().createdAt!!.isBefore(LocalDateTime.of(2025, 9, 6, 0, 0)))
    }

    @Test
    fun test007c_search_by_nombreProducto_not_found_returns_empty_page() {
        val pageable = PageRequest.of(0, 10)
        whenever(productoOrdenCompraRepository.findAll(any(), any<Pageable>()))
            .thenReturn(PageImpl(emptyList()))

        val filter = ProductCompraFilterDto(nombreProducto = "NoExiste")
        val page = service.search(filter, pageable)

        assertEquals(0, page.totalElements)
        assertTrue(page.content.isEmpty())
    }
}
