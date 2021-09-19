package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.Vehicle
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class VehicleProvider {
    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child("Usuarios").child("Repartidor")

    fun create(vehicle: Vehicle): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = vehicle.id
        map["dni"] = vehicle.dni
        map["nombre"] = vehicle.nombre
        map["email"] = vehicle.email
        return mDatabase.child(vehicle.id).setValue(map)
    }

    fun update(vehicle: Vehicle): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["perfil"] = vehicle.perfil
        return mDatabase.child(vehicle.id).updateChildren(map)
    }

    fun getDriver(id: String): DatabaseReference {
        return mDatabase.child(id)
    }
}