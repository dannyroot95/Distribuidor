package com.aukde.distribuidor.Models

data class Worker (
    val id : String = "",
    val dni : String = "",
    val nombre : String = "",
    var tipoUsuario : String = "",
    val perfil : String = "",
    val email : String = "",
    val password : String = "")
