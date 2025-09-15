package com.example.tinten.ordenPago.service

import com.example.tinten.exception.domainExceptions.NotFoundException
import com.example.tinten.ordenPago.dto.OrdenPagoDto
import com.example.tinten.ordenPago.entity.OrdenPago
import com.example.tinten.ordenPago.repository.OrdenPagoRepository
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class OrdenPagoPdfService(
    private val ordenPagoRepository: OrdenPagoRepository,
    private val templateEngine: TemplateEngine
) {

    fun exportarPdf(ordenId: Long): ByteArray {
        val opEntity = ordenPagoRepository.findById(ordenId)
            .orElseThrow { NotFoundException("Orden de pago $ordenId no encontrada") }

        // Usá el mapeo que tengas (instance/companion). Ajustá si tu método es diferente.
        val dto: OrdenPagoDto = OrdenPago.toDto(opEntity)

        // 2) Fechas y número visible (OP nnn/yyyy)
        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaCarga = dto.fechaCarga.format(fmt)
        val fechaFactura = dto.fechaFactura.format(fmt)
        val fechaCreacion = (dto.fechaCreacion ?: dto.fechaCarga).format(fmt)
        val opNumero = "${dto.id}/${(dto.fechaCreacion ?: dto.fechaCarga).year}"

        // 3) Importe, retenciones y neto
        val zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        val importe = dto.importe.setScale(2, RoundingMode.HALF_UP)
        val ret = (dto.retenciones ?: zero).setScale(2, RoundingMode.HALF_UP)
        val neto = importe.subtract(ret).max(zero)

        // 4) Formateo moneda (toma moneda local; si querés, podés fijar ARS)
        val nf = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
            maximumFractionDigits = 2
        }

        // 5) Variables para la vista
        val ctx = Context(Locale("es", "AR")).apply {
            setVariable("op", dto)                    // DTO completo (proveedor, cuit, etc.)
            setVariable("opNumero", opNumero)
            setVariable("fechaCarga", fechaCarga)
            setVariable("fechaFactura", fechaFactura)
            setVariable("fechaCreacion", fechaCreacion)
            setVariable("importe", nf.format(importe))
            setVariable("retenciones", if (ret > zero) nf.format(ret) else "—")
            setVariable("neto", nf.format(neto))
            setVariable("metodoPago", dto.metodoPago ?: "—")
            setVariable("certificado", dto.certificado ?: "—")
        }

        // 6) Render HTML con Thymeleaf
        val html = templateEngine.process("orden_pago", ctx)

        // 7) Convertir a PDF con openhtmltopdf
        val baos = ByteArrayOutputStream()
        PdfRendererBuilder()
            .useFastMode()
            .withHtmlContent(html, null)
            .toStream(baos)
            .run()

        return baos.toByteArray()
    }
}
