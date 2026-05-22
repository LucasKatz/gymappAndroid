package com.example.clubdeportivodam



data class Actividad(
    val id: Int,
    val nombre: String,
    val profesor: String,
    val horario1: String,
    val horario2: String,
    val cupos: Int
)