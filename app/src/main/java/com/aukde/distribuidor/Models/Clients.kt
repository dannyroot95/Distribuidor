package com.aukde.distribuidor.Models

data class Clients  (
    val id : String = "",
    val dni : String = "",
    val nombre : String = "",
    val codigo : String = "",
    val zona : String = "",
    val telefono : String = "",
    val direccion : String = "",
    val latitud : Double = 0.0,
    val longitud : Double = 0.0)