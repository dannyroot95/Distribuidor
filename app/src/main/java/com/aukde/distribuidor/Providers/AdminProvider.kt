package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.Admin
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AdminProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child("Usuarios").child("Administrador")

    fun create(admin: Admin): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = admin.id
        map["dni"] = admin.dni
        map["nombre"] = admin.nombre
        map["email"] = admin.email
        return mDatabase.child(admin.id).setValue(map)
    }

    fun update(admin: Admin): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["perfil"] = admin.perfil
        return mDatabase.child(admin.id).updateChildren(map)
    }

    fun getClient(idAdmin: String): DatabaseReference {
        return mDatabase.child(idAdmin)
    }

}