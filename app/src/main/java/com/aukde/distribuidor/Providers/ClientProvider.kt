package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.Clients
import com.aukde.distribuidor.Models.Product
import com.aukde.distribuidor.UI.Manager.ManageClients
import com.aukde.distribuidor.UI.Manager.ManageProductActivity
import com.aukde.distribuidor.Utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.util.HashMap

class ClientProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(Constants.CLIENTS)

    fun create(client: Clients): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = client.id
        map["nombre"] = client.nombre
        map["codigo"] = client.codigo
        map["dni"] = client.dni
        map["telefono"] = client.telefono
        map["direccion"] = client.direccion
        map["zona"] = client.zona
        map["latitud"] = client.latitud
        map["longitud"] = client.longitud
        return mDatabase.child(client.id).setValue(map)
    }

    fun getClientList(activity : ManageClients){
        mDatabase.get().addOnSuccessListener { snapshot ->
            val clientList: ArrayList<Clients> = ArrayList()
            for (i in snapshot.children){
                val product = i.getValue<Clients>()!!
                clientList.add(product)
            }
            activity.successClientList(clientList)
        }
    }

    fun getClient(id: String): DatabaseReference {
        return mDatabase.child(id)
    }
}