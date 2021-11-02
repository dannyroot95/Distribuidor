package com.aukde.distribuidor.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id : String = "",
    val dni : String = "",
    val nombre : String = "",
    val tipoUsuario : String = "",
    val perfil : String = "",
    val email : String = "",
    val password : String = "") : Parcelable
