package com.example.tinten.producto.dto

import org.springframework.data.domain.Page

data class ProductoOrderSearchResultDto(
    val page: Page<ProductOrderDto>,
    val totals: ProductoOrderTotalsDto
)