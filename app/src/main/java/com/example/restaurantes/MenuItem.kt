package com.example.restaurantes

data class MenuItem(
    val identificador: String? = null,
    val calificacion: Double? = null,
    val nomPlato: String? = null,
    val nomRestaurante: String? = null,
    val precio: Double? = null,
    val restauranteId: String? = null // Agregado para la relación
)