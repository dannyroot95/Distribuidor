package com.aukde.distribuidor.Providers

import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.UI.Manager.ManageUsersActivity
import com.aukde.distribuidor.Utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.HashMap

class SuperUserProvider {

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

    fun getIdNumberClient(){

    }

    /**
     * A function to get the products list from firebase.
     *
     * @param activity The fragment is passed as parameter as the function is called from activity and need to the success result.
     */
    fun getUserList(activity : ManageUsersActivity){
        mDatabase.get().addOnSuccessListener { snapshot ->
            val userList: ArrayList<User> = ArrayList()
            for (i in snapshot.children){
                val users = i.getValue<User>()!!
                userList.add(users)
            }
            activity.successUsersList(userList)
        }
    }

    fun getData(id: String) : DatabaseReference {
        return mDatabase.child(id)
    }

}