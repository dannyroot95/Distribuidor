package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.Worker
import com.aukde.distribuidor.Utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class WorkerProvider {
    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(Constants.USERS)

    fun create(worker: Worker): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        worker.tipoUsuario = Constants.WORKER
        map["id"] = worker.id
        map["dni"] = worker.dni
        map["tipoUsuario"] = worker.tipoUsuario
        map["nombre"] = worker.nombre
        map["email"] = worker.email
        return mDatabase.child(worker.id).setValue(map)
    }

    fun update(worker: Worker): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["perfil"] = worker.perfil
        return mDatabase.child(worker.id).updateChildren(map)
    }

    fun getWorker(idWorker: String): DatabaseReference {
        return mDatabase.child(idWorker)
    }
}