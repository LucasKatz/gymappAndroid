package com.example.clubdeportivodam

// Este es el modelo de datos que elimina el error "Unresolved reference Socio"
data class Socio(
    val dni: String,
    val nombre: String,
    val Email: String,
    val telefono: String,
    val categoria: String,
    val vencimiento: Long, // Debe ser Long
    val monto: Double,     // ¡No olvides este!
    val estado: String
)