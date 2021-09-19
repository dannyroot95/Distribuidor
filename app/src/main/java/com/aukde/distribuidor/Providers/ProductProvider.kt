package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.Admin
import com.aukde.distribuidor.Models.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class ProductProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child("Productos")

    fun create(product: Product): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = product.id
        map["nombre"] = product.nombre
        map["codigo"] = product.codigo
        map["imagen"] = product.imagen
        map["stock"] = product.stock
        return mDatabase.child(product.id).setValue(map)
    }

    fun update(product: Product): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["nombre"] = product.nombre
        map["codigo"] = product.codigo
        map["imagen"] = product.imagen
        map["stock"] = product.stock
        return mDatabase.child(product.id).setValue(map)
    }

    fun getProduct(id: String): DatabaseReference {
        return mDatabase.child(id)
    }

}