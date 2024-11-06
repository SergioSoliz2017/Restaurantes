package com.example.restaurantes

import java.util.Date

data class Oferta(
    var descripcion: String = "",
    var fechaFin: Date = Date(),
    var fechaInicio: Date = Date(),
    var imagen: String = "",
    var menuId: String = "",
    var precio: Double = 0.0,
    var restauranteId: String = "",
    var titulo: String = ""
) {
    constructor() : this("", Date(), Date(), "", "", 0.0, "", "")
}