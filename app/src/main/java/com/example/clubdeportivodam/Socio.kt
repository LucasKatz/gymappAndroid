package com.example.clubdeportivodam

// Este es el modelo de datos que elimina el error "Unresolved reference Socio"
data class Socio(
    val dni: String,
    val nombre: String,
    val categoria: String,
    val vencimiento: String,
    val telefono: String,
    val estado: String
)