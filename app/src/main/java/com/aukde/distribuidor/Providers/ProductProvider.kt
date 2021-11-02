package com.aukde.distribuidor.Providers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.aukde.distribuidor.Models.Product
import com.aukde.distribuidor.UI.Manager.ManageProductActivity
import com.aukde.distribuidor.Utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.HashMap

class ProductProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(Constants.PRODUCTS)

    private val mFireStore = FirebaseFirestore.getInstance()

    fun create(product: Product , activity : Activity , ID : String){
        mFireStore.collection(Constants.PRODUCTS).document(ID).set(product, SetOptions.merge())
            .addOnSuccessListener {
            // Here call a function of base activity for transferring the result to it.
            Toast.makeText(activity,"Producto Subido!",Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener { e ->
                Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }


    fun geProductList(activity : ManageProductActivity){
        mFireStore.collection(Constants.PRODUCTS).get().addOnSuccessListener { snapshot ->
            val productList: ArrayList<Product> = ArrayList()
            for (i in snapshot.documents){
                val product = i.toObject(Product::class.java)!!
              productList.add(product)
            }
            activity.successProductList(productList)
        }
    }

    fun getProduct(id: String): DatabaseReference {
        return mDatabase.child(id)
    }

}