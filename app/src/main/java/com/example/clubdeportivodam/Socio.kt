package com.example.clubdeportivodam

// Modelo de datos del objeto SOCIO
data class Socio(
    val dni: String,
    val nombre: String,
    val Email: String,
    val telefono: String,
    val categoria: String,
    val vencimiento: Long,
    val monto: Double,
    val estado: String
)