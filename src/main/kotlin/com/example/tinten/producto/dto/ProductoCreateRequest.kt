package com.example.tinten.producto.dto

data class ProductoCreateRequest(
    val nombre: String,
    val marca: String,
    val proveedor: String,
    val envase: String,
)