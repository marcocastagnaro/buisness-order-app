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

        // -----------------------------------------
        // 1) Productos alimenticios (marcas reales)
        // -----------------------------------------
        val productos = listOf(
            // arroz
            Producto(nombre = "Arroz Largo Fino", marca = "Gallo", proveedor = "Molinos Río de la Plata", envase = "1 kg"),
            Producto(nombre = "Arroz Doble Carolina", marca = "Gallo", proveedor = "Molinos Río de la Plata", envase = "1 kg"),
            // fideos/pastas
            Producto(nombre = "Fideos Spaghetti", marca = "Matarazzo", proveedor = "Molinos Río de la Plata", envase = "500 g"),
            Producto(nombre = "Fideos Tirabuzón", marca = "Lucchetti", proveedor = "Molinos Río de la Plata", envase = "500 g"),
            // harinas
            Producto(nombre = "Harina 000", marca = "Morixe", proveedor = "Morixe", envase = "1 kg"),
            Producto(nombre = "Harina 0000", marca = "Blancaflor", proveedor = "Molinos Río de la Plata", envase = "1 kg"),
            // aceites
            Producto(nombre = "Aceite de Girasol", marca = "Natura", proveedor = "Molinos Río de la Plata", envase = "900 ml"),
            Producto(nombre = "Aceite de Soja", marca = "Marolio", proveedor = "Molinos Marolio", envase = "900 ml"),
            // azúcar
            Producto(nombre = "Azúcar Común", marca = "Ledesma", proveedor = "Ledesma", envase = "1 kg"),
            // lácteos
            Producto(nombre = "Leche Entera", marca = "La Serenísima", proveedor = "Mastellone Hnos", envase = "1 L"),
            Producto(nombre = "Yogur Entero Vainilla", marca = "La Serenísima", proveedor = "Mastellone Hnos", envase = "190 g"),
            // enlatados / tomates
            Producto(nombre = "Puré de Tomate", marca = "Molto", proveedor = "Molto", envase = "520 g"),
            Producto(nombre = "Tomate Triturado", marca = "Arcor", proveedor = "Arcor", envase = "520 g"),
            // legumbres
            Producto(nombre = "Lentejas Secas", marca = "Arcor", proveedor = "Arcor", envase = "400 g"),
            // caldos
            Producto(nombre = "Caldo de Verduras", marca = "Knorr", proveedor = "Unilever", envase = "6 cubos"),
            // galletitas/panificados
            Producto(nombre = "Galletitas Agua", marca = "Bagley", proveedor = "Arcor", envase = "3 x 100 g"),
            Producto(nombre = "Pan de Molde", marca = "Fargo", proveedor = "Fargo", envase = "600 g"),
            // infusiones
            Producto(nombre = "Yerba Mate", marca = "Taragüí", proveedor = "Las Marías", envase = "1 kg"),
            Producto(nombre = "Café Molido", marca = "La Virginia", proveedor = "La Virginia", envase = "500 g"),
            // extras
            Producto(nombre = "Arvejas en Lata", marca = "Marolio", proveedor = "Molinos Marolio", envase = "350 g"),
            Producto(nombre = "Aceitunas Verdes", marca = "Nucete", proveedor = "Nucete", envase = "350 g")
        )

        val savedProductos = productoRepository.saveAll(productos)
        val byName = savedProductos.associateBy { it.nombre to it.marca } // para búsquedas rápidas

        fun p(nombre: String, marca: String) = byName[nombre to marca]!!

        // -----------------------------------------
        // 2) Órdenes de Compra con ítems
        // -----------------------------------------
        val oc1 = OrdenCompra(
            proveedorRazonSocial = "Molinos Río de la Plata S.A.",
            atencionDe = "Compras",
            fecha = LocalDate.now().minusDays(15),
            moneda = "ARS",
            transporte = "Camión",
            fechaCreacion = LocalDate.now().minusDays(15),
            domicilio = "Av los lagos 3115"
        )
        oc1.productos.addAll(
            listOf(
                ProductoOrdenCompra(producto = p("Arroz Largo Fino", "Gallo"), ordenCompra = oc1, precioUnitario = BigDecimal("1200.00"), cantidad = 50),
                ProductoOrdenCompra(producto = p("Fideos Spaghetti", "Matarazzo"), ordenCompra = oc1, precioUnitario = BigDecimal("900.00"), cantidad = 80),
                ProductoOrdenCompra(producto = p("Aceite de Girasol", "Natura"), ordenCompra = oc1, precioUnitario = BigDecimal("1800.00"), cantidad = 40)
            )
        )

        val oc2 = OrdenCompra(
            proveedorRazonSocial = "Arcor S.A.I.C.",
            atencionDe = "Abastecimiento",
            fecha = LocalDate.now().minusDays(12),
            moneda = "ARS",
            transporte = "Flete Tercerizado",
            fechaCreacion = LocalDate.now().minusDays(12),
            domicilio = "Av los lagos 3115"
        )
        oc2.productos.addAll(
            listOf(
                ProductoOrdenCompra(producto = p("Tomate Triturado", "Arcor"), ordenCompra = oc2, precioUnitario = BigDecimal("950.00"), cantidad = 120),
                ProductoOrdenCompra(producto = p("Lentejas Secas", "Arcor"), ordenCompra = oc2, precioUnitario = BigDecimal("1100.00"), cantidad = 60),
                ProductoOrdenCompra(producto = p("Galletitas Agua", "Bagley"), ordenCompra = oc2, precioUnitario = BigDecimal("700.00"), cantidad = 90)
            )
        )

        val oc3 = OrdenCompra(
            proveedorRazonSocial = "Mastellone Hnos S.A.",
            atencionDe = "Recepción",
            fecha = LocalDate.now().minusDays(10),
            moneda = "ARS",
            transporte = "Refrigerado",
            fechaCreacion = LocalDate.now().minusDays(10),
            domicilio = "Av los lagos 3115"
        )
        oc3.productos.addAll(
            listOf(
                ProductoOrdenCompra(producto = p("Leche Entera", "La Serenísima"), ordenCompra = oc3, precioUnitario = BigDecimal("800.00"), cantidad = 200),
                ProductoOrdenCompra(producto = p("Yogur Entero Vainilla", "La Serenísima"), ordenCompra = oc3, precioUnitario = BigDecimal("450.00"), cantidad = 150)
            )
        )

        val oc4 = OrdenCompra(
            proveedorRazonSocial = "Molto S.A.",
            atencionDe = "Compras",
            fecha = LocalDate.now().minusDays(8),
            moneda = "ARS",
            transporte = "Camión",
            fechaCreacion = LocalDate.now().minusDays(8),
            domicilio = "Av los lagos 3115"
        )
        oc4.productos.addAll(
            listOf(
                ProductoOrdenCompra(producto = p("Puré de Tomate", "Molto"), ordenCompra = oc4, precioUnitario = BigDecimal("700.00"), cantidad = 200),
                ProductoOrdenCompra(producto = p("Aceitunas Verdes", "Nucete"), ordenCompra = oc4, precioUnitario = BigDecimal("2200.00"), cantidad = 30)
            )
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
        oc5.productos.addAll(
            listOf(
                ProductoOrdenCompra(producto = p("Aceite de Soja", "Marolio"), ordenCompra = oc5, precioUnitario = BigDecimal("1600.00"), cantidad = 60),
                ProductoOrdenCompra(producto = p("Arvejas en Lata", "Marolio"), ordenCompra = oc5, precioUnitario = BigDecimal("600.00"), cantidad = 140),
                ProductoOrdenCompra(producto = p("Yerba Mate", "Taragüí"), ordenCompra = oc5, precioUnitario = BigDecimal("4200.00"), cantidad = 40)
            )
        )

        val savedOCs = ordenCompraRepository.saveAll(listOf(oc1, oc2, oc3, oc4, oc5))

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
