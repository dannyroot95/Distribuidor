package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AdminProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(Constants.USERS)

    fun create(user: User): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = user.id
        map["dni"] = user.dni
        map["tipoUsuario"] = user.tipoUsuario
        map["nombre"] = user.nombre
        map["email"] = user.email
        return mDatabase.child(user.id).setValue(map)
    }

    fun update(user: User): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["perfil"] = user.perfil
        return mDatabase.child(user.id).updateChildren(map)
    }

    fun getClient(idAdmin: String): DatabaseReference {
        return mDatabase.child(idAdmin)
    }

}