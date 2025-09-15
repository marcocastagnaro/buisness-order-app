package com.example.tinten.ordenCompra.service

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.ordenCompra.repository.OrdenCompraRepository
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder

@Service
class OrdenCompraPdfService(
    private val repo: OrdenCompraRepository,
    private val templateEngine: TemplateEngine
) {
    // Configurable (Argentina suele 21%)
    private val ivaRate = BigDecimal("0.21")

    fun exportarPdf(ordenId: Long): ByteArray {
        val oc = repo.findById(ordenId).orElseThrow { NotFoundException("OC $ordenId no encontrada") }
        val dto = oc.toDto()

        val fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val ocNumero = "${dto.id}/${dto.fecha.year}"
        val fecha = dto.fecha.format(fmt)
        val fechaCreacion = dto.fechaCreacion.format(fmt)

// Totales
        val subtotalBD = dto.productos.fold(java.math.BigDecimal.ZERO) { acc, p ->
            val unit = p.precioUnitario ?: java.math.BigDecimal.ZERO
            val qty  = java.math.BigDecimal(p.cantidad ?: 0)
            acc + unit.multiply(qty)
        }.setScale(2, java.math.RoundingMode.HALF_UP)

        val ivaBD = subtotalBD.multiply(java.math.BigDecimal("0.21")).setScale(2, java.math.RoundingMode.HALF_UP)
        val totalBD = subtotalBD.add(ivaBD)

        val nf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es","AR")).apply {
            currency = runCatching { java.util.Currency.getInstance(dto.moneda) }.getOrElse { java.util.Currency.getInstance("ARS") }
            maximumFractionDigits = 2
        }

        val ctx = org.thymeleaf.context.Context(java.util.Locale("es","AR")).apply {
            setVariable("oc", dto)                // ⇐ el DTO completo (p.productoNombre, etc.)
            setVariable("ocNumero", ocNumero)
            setVariable("fecha", fecha)
            setVariable("fechaCreacion", fechaCreacion)
            setVariable("subtotal", nf.format(subtotalBD))
            setVariable("iva", nf.format(ivaBD))
            setVariable("total", nf.format(totalBD))
            setVariable("moneda", dto.moneda)
            // si querés mostrar un contacto fijo:
            setVariable("contactoInfo", "+54 11 3452-52122")
        }

        val html = templateEngine.process("orden_compra", ctx)

        val baos = java.io.ByteArrayOutputStream()
        com.openhtmltopdf.pdfboxout.PdfRendererBuilder()
            .useFastMode()
            .withHtmlContent(html, null)
            .toStream(baos)
            .run()

        return baos.toByteArray()
    }
}
