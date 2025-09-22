package com.example.tinten.config

import com.example.tinten.ordenCompra.entity.OrdenCompra
import com.example.tinten.ordenPago.entity.OrdenPago
import com.example.tinten.producto.entity.Producto
import com.example.tinten.producto.entity.ProductoOrdenCompra
import com.example.tinten.ordenCompra.repository.OrdenCompraRepository
import com.example.tinten.ordenPago.repository.OrdenPagoRepository
import com.example.tinten.producto.repository.ProductoRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Configuration
@Profile("dev")
class DataSeederConfig {

    @Bean
    fun seedDataRunner(
        productoRepository: ProductoRepository,
        ordenCompraRepository: OrdenCompraRepository,
        ordenPagoRepository: OrdenPagoRepository
    ) = CommandLineRunner {
        seedIfEmpty(productoRepository, ordenCompraRepository, ordenPagoRepository)
    }

    @Transactional
    fun seedIfEmpty(
        productoRepository: ProductoRepository,
        ordenCompraRepository: OrdenCompraRepository,
        ordenPagoRepository: OrdenPagoRepository
    ) {
        if (productoRepository.count() > 0L || ordenCompraRepository.count() > 0L || ordenPagoRepository.count() > 0L) {
            return
        }

        fun month(n: Long) = LocalDate.now().minusMonths(n).withDayOfMonth(1)

        // ---------- Productos base ----------
        val productos = listOf(
            Producto(nombre = "Arroz Largo Fino", marca = "Gallo", proveedor = "Molinos Río de la Plata", envase = "1 kg"),
            Producto(nombre = "Aceite de Girasol", marca = "Natura", proveedor = "Molinos Río de la Plata", envase = "900 ml"),
            Producto(nombre = "Leche Entera", marca = "La Serenísima", proveedor = "Mastellone Hnos", envase = "1 L"),
            Producto(nombre = "Tomate Triturado", marca = "Arcor", proveedor = "Arcor", envase = "520 g"),
            Producto(nombre = "Fideos Spaghetti", marca = "Matarazzo", proveedor = "Molinos Río de la Plata", envase = "500 g"),
            Producto(nombre = "Yerba Mate", marca = "Taragüí", proveedor = "Las Marías", envase = "1 kg"),
            Producto(nombre = "Arvejas en Lata", marca = "Marolio", proveedor = "Molinos Marolio", envase = "350 g"),
            Producto(nombre = "Aceitunas Verdes", marca = "Nucete", proveedor = "Nucete", envase = "350 g"),
            // podés sumar más...
        )

        val savedProductos = productoRepository.saveAll(productos)
        val byName = savedProductos.associateBy { it.nombre to it.marca }
        fun p(nombre: String, marca: String) = byName[nombre to marca]!!

        // Helper para agregar ítems con fecha propia
        fun addItem(
            oc: OrdenCompra,
            nombre: String,
            marca: String,
            precio: String,
            cantidad: Int,
            createdAt: LocalDate
        ) = ProductoOrdenCompra(
            producto = p(nombre, marca),
            ordenCompra = oc,
            precioUnitario = BigDecimal(precio),
            cantidad = cantidad,
            createdAt = createdAt
        )

        // ---------- OCs ----------
        val oc1 = OrdenCompra(
            proveedorRazonSocial = "Molinos Río de la Plata S.A.",
            atencionDe = "Compras",
            fecha = LocalDate.now().minusDays(90),
            moneda = "ARS",
            transporte = "Camión",
            fechaCreacion = LocalDate.now().minusDays(90),
            domicilio = "Av los lagos 3115"
        )

        val oc2 = OrdenCompra(
            proveedorRazonSocial = "Arcor S.A.I.C.",
            atencionDe = "Abastecimiento",
            fecha = LocalDate.now().minusDays(60),
            moneda = "ARS",
            transporte = "Flete Tercerizado",
            fechaCreacion = LocalDate.now().minusDays(60),
            domicilio = "Av los lagos 3115"
        )

        val oc3 = OrdenCompra(
            proveedorRazonSocial = "Mastellone Hnos S.A.",
            atencionDe = "Recepción",
            fecha = LocalDate.now().minusDays(30),
            moneda = "ARS",
            transporte = "Refrigerado",
            fechaCreacion = LocalDate.now().minusDays(30),
            domicilio = "Av los lagos 3115"
        )

        val oc4 = OrdenCompra(
            proveedorRazonSocial = "Molto S.A.",
            atencionDe = "Compras",
            fecha = LocalDate.now().minusDays(15),
            moneda = "ARS",
            transporte = "Camión",
            fechaCreacion = LocalDate.now().minusDays(15),
            domicilio = "Av los lagos 3115"
        )

        val oc5 = OrdenCompra(
            proveedorRazonSocial = "Molinos Marolio S.A.",
            atencionDe = "Compras",
            fecha = LocalDate.now().minusDays(5),
            moneda = "ARS",
            transporte = "Camión",
            fechaCreacion = LocalDate.now().minusDays(5),
            domicilio = "Av los lagos 3115"
        )

        // ---------- Ítems (mismos productos repetidos en distintos meses/OCs) ----------
        oc1.productos.addAll(listOf(
            // Arroz en 3 meses distintos
            addItem(oc1, "Arroz Largo Fino", "Gallo", "1200.00", 50, month(5)),  // más viejo
            addItem(oc1, "Fideos Spaghetti", "Matarazzo", "900.00", 80, month(5)),
            addItem(oc1, "Aceite de Girasol", "Natura", "1800.00", 40, month(5)),
        ))

        oc2.productos.addAll(listOf(
            addItem(oc2, "Arroz Largo Fino", "Gallo", "1350.00", 60, month(4)),  // sube precio vs mes 5
            addItem(oc2, "Tomate Triturado", "Arcor", "950.00", 120, month(4)),
            addItem(oc2, "Aceite de Girasol", "Natura", "1900.00", 30, month(4)), // aceite repetido
        ))

        oc3.productos.addAll(listOf(
            addItem(oc3, "Arroz Largo Fino", "Gallo", "1500.00", 70, month(3)),  // vuelve a repetirse
            addItem(oc3, "Leche Entera", "La Serenísima", "800.00", 200, month(3)),
            addItem(oc3, "Aceite de Girasol", "Natura", "2100.00", 35, month(3)), // aceite repetido
        ))

        oc4.productos.addAll(listOf(
            addItem(oc4, "Leche Entera", "La Serenísima", "860.00", 220, month(2)), // leche repetida
            addItem(oc4, "Aceitunas Verdes", "Nucete", "2200.00", 30, month(2)),
            addItem(oc4, "Tomate Triturado", "Arcor", "1000.00", 100, month(2)),   // tomate repetido
        ))

        oc5.productos.addAll(listOf(
            addItem(oc5, "Arroz Largo Fino", "Gallo", "1650.00", 55, month(1)),   // arroz repetido (mes reciente)
            addItem(oc5, "Yerba Mate", "Taragüí", "4200.00", 40, month(1)),
            addItem(oc5, "Arvejas en Lata", "Marolio", "600.00", 140, LocalDate.now().withDayOfMonth(1)),
        ))

        ordenCompraRepository.saveAll(listOf(oc1, oc2, oc3, oc4, oc5))
        // -----------------------------------------
        // 3) Órdenes de Pago (simuladas)
        // -----------------------------------------
        // Nota: estos pagos no están “linkeados” a OC en tu modelo actual. Usamos datos consistentes.
        val pagos = listOf(
            OrdenPago(
                fechaCarga = LocalDate.now().minusDays(14),
                proveedor = "Molinos Río de la Plata S.A.",
                razonSocial = "Molinos Río de la Plata S.A.",
                cuit = "30-50000666-5",     // ejemplo
                concepto = "Pago OC Molinos - insumos arroz/fideos/aceite",
                fechaFactura = LocalDate.now().minusDays(14),
                numeroFactura = "0001-00000001",
                importe = BigDecimal("250000.00"),
                retenciones = BigDecimal("12500.00"),
                certificado = "Cert-AR-001",
                fechaCreacion = LocalDate.now().minusDays(14),
                metodoPago = "Transferencia"
            ),
            OrdenPago(
                fechaCarga = LocalDate.now().minusDays(11),
                proveedor = "Arcor S.A.I.C.",
                razonSocial = "Arcor S.A.I.C.",
                cuit = "30-70700477-3",
                concepto = "Pago OC Arcor - tomate/lentejas/galletitas",
                fechaFactura = LocalDate.now().minusDays(11),
                numeroFactura = "0001-00000002",
                importe = BigDecimal("220000.00"),
                retenciones = BigDecimal.ZERO, // sin retenciones
                certificado = null,
                fechaCreacion = LocalDate.now().minusDays(11),
                metodoPago = "Transferencia"
            ),
            OrdenPago(
                fechaCarga = LocalDate.now().minusDays(9),
                proveedor = "Mastellone Hnos S.A.",
                razonSocial = "Mastellone Hnos S.A.",
                cuit = "30-50167865-9",
                concepto = "Pago OC Mastellone - lácteos",
                fechaFactura = LocalDate.now().minusDays(9),
                numeroFactura = "0001-00000003",
                importe = BigDecimal("180000.00"),
                retenciones = BigDecimal("9000.00"),
                certificado = "Cert-AR-002",
                fechaCreacion = LocalDate.now().minusDays(9),
                metodoPago = "Transferencia"
            ),
            OrdenPago(
                fechaCarga = LocalDate.now().minusDays(7),
                proveedor = "Molto S.A.",
                razonSocial = "Molto S.A.",
                cuit = "30-61123456-7",
                concepto = "Pago OC Molto - tomate/aceitunas",
                fechaFactura = LocalDate.now().minusDays(7),
                numeroFactura = "0001-00000004",
                importe = BigDecimal("160000.00"),
                retenciones = BigDecimal.ZERO,
                certificado = null,
                fechaCreacion = LocalDate.now().minusDays(7),
                metodoPago = "Cheque"
            ),
            OrdenPago(
                fechaCarga = LocalDate.now().minusDays(4),
                proveedor = "Molinos Marolio S.A.",
                razonSocial = "Molinos Marolio S.A.",
                cuit = "30-60234567-6",
                concepto = "Pago OC Marolio - aceites/arvejas/yerba",
                fechaFactura = LocalDate.now().minusDays(4),
                numeroFactura = "0001-00000005",
                importe = BigDecimal("300000.00"),
                retenciones = BigDecimal("15000.00"),
                certificado = "Cert-AR-003",
                fechaCreacion = LocalDate.now().minusDays(4),
                metodoPago = "Transferencia"
            )
        )

        ordenPagoRepository.saveAll(pagos)
    }
}
