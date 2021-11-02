package com.aukde.distribuidor.UI.Registers

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aukde.distribuidor.Models.Product
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Providers.ImageProvider
import com.aukde.distribuidor.Providers.ProductProvider
import com.aukde.distribuidor.R
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.Utils.FileUtil
import com.aukde.distribuidor.databinding.ActivityProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*


class ProductActivity : BaseActivity() {

    private var mImageFileProfile: File? = null
    private lateinit var mImageProvider: ImageProvider
    var urlImageProfile = ""
    private lateinit var mProductProvider: ProductProvider
    private var metric : String = ""
    private var subMetric: String = ""
    private var magnitude : String = ""
    private var category : String  = ""
    private var idCompany : String = ""
    private var mdatabase = Firebase.database.reference.child("Usuarios")
    private var mAuth : AuthenticationProvider = AuthenticationProvider()

    private lateinit var binding : ActivityProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mImageProvider = ImageProvider("products")
        mProductProvider = ProductProvider()
        getIdCompany()

        val adapterSpinnerMetrics = ArrayAdapter.createFromResource(
                this,R.array.metrics,
                R.layout.support_simple_spinner_dropdown_item)
        binding.spMetrics.adapter = adapterSpinnerMetrics
        binding.spMetrics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                metric = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}

        val adapterSpinnerSubMetrics = ArrayAdapter.createFromResource(
                this,R.array.metrics,
                R.layout.support_simple_spinner_dropdown_item)
        binding.spSubMetrics.adapter = adapterSpinnerSubMetrics
        binding.spSubMetrics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                subMetric = parent!!.getItemAtPosition(position).toString()
                if (subMetric == "Unidad"){
                    binding.tilSubUnit.visibility = View.GONE
                }
                else{
                    binding.tilSubUnit.visibility = View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}

        val adapterSpinnerMagnitude = ArrayAdapter.createFromResource(
                this,
                R.array.magnitude,
                R.layout.support_simple_spinner_dropdown_item)
        binding.spMagnitude.adapter = adapterSpinnerMagnitude
        binding.spMagnitude.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                magnitude = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}

        val adapterSpinnerCategory = ArrayAdapter.createFromResource(
                this,
                R.array.category,
                R.layout.support_simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapterSpinnerCategory
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                category = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}

        binding.ivAddImageProduct.setOnClickListener {
            openGallery()
        }
        binding.btnRegisterProduct.setOnClickListener {
            register()
        }

    }

    private fun getIdCompany() {
        mdatabase.child(mAuth.getId()).addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val random = (100000..999999).random()
                    idCompany = snapshot.child("id_company").value.toString()
                    binding.edtCodeProduct.setText("$idCompany-$random")
                }
                else{
                    Toast.makeText(this@ProductActivity,"No existe id de negocio",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
               getIdCompany()
            }
        })
    }

    private fun openGallery() {
        if (binding.edtNameProduct.text.toString().isNotEmpty()){
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, Constants.GALLERY_REQUEST)
        }
        else{
            Toast.makeText(this, "Ingrese el nombre del producto...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                showDialog("Subiendo foto...")
                mImageFileProfile = FileUtil().from(this, Objects.requireNonNull(data)!!.data!!)
                mImageProvider.saveImage(
                    this,
                    mImageFileProfile!!,
                    binding.edtNameProduct.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uri ->
                            urlImageProfile = uri.toString()
                            binding.ivProductImage.setImageBitmap(
                                BitmapFactory.decodeFile(
                                    mImageFileProfile!!.absolutePath
                                )
                            )
                            hideDialog()
                            Toast.makeText(this, "Imagen subida!", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        hideDialog()
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }
                }.addOnCanceledListener {
                    hideDialog()
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                hideDialog()
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register() {

        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val ID = List(20) { alphabet.random() }.joinToString("")

        val nameProduct = binding.edtNameProduct.text.toString()
        val code = binding.edtCodeProduct.text.toString()
        val quantity = binding.edtQuantity.text.toString()
        val unit = binding.edtUnit.text.toString()
        val sub_unit = binding.edtSubUnit.text.toString()
        val quantity_magnitude = binding.edtQuantityMagnitude.text.toString()
        val price_whole = binding.edtPriceWhole.text.toString()
        val price_pack = binding.edtPricePack.text.toString()
        val price_unit = binding.edtPriceUnit.text.toString()


        if (nameProduct.isNotEmpty() && code.isNotEmpty() && quantity.isNotEmpty() && price_whole.isNotEmpty()
            && price_pack.isNotEmpty() && urlImageProfile != ""){
            showDialog("Registrando producto...")

            val product = Product(ID,
                    idCompany,
                    urlImageProfile,
                    nameProduct,
                    category,
                    code,
                    quantity,
                    metric,
                    unit,
                    subMetric,
                    sub_unit,
                    quantity_magnitude,
                    magnitude,
                    price_whole,
                    price_pack,
                    price_unit)

            mProductProvider.create(product,this,ID)
            hideDialog()

        }

    }


}