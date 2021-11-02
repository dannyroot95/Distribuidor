package com.aukde.distribuidor.UI.Registers

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Models.DNI
import com.aukde.distribuidor.Models.Vehicle
import com.aukde.distribuidor.Models.Worker
import com.aukde.distribuidor.Network.InterfaceDNI
import com.aukde.distribuidor.Providers.*
import com.aukde.distribuidor.R
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.databinding.ActivityRegisterUsersBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterUsersActivity : BaseActivity() {

    private lateinit var binding : ActivityRegisterUsersBinding
    private var mAuth : AuthenticationProvider = AuthenticationProvider()
    private var mAdmin : AdminProvider = AdminProvider()
    private var mWorker : WorkerProvider = WorkerProvider()
    private var mDelivery : VehicleProvider = VehicleProvider()
    private var mTypeUser : String = ""
    private var mEmail : String = ""
    private var mPassword : String = ""

    var URL = "https://dniruc.apisperu.com/api/v1/dni/"
    var BASE_TOKEN = "?token="
    var TOKEN = BASE_TOKEN + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNhbWFuLmRhbm55OTVAZ21haWwuY29tIn0.J7apbfAgC6PK_L9EJBkJWMdJmHZZxYWVr2HFEp8WqLQ"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferenceEmail : SharedPreferences = getSharedPreferences(Constants.EMAIL,MODE_PRIVATE)
        mEmail = preferenceEmail.getString(Constants.KEY_EMAIL,"").toString()

        val preferencePassword : SharedPreferences = getSharedPreferences(Constants.PASSWORD,MODE_PRIVATE)
        mPassword = preferencePassword.getString(Constants.KEY_PASSWORD,"").toString()

        val adapterSpinner = ArrayAdapter.createFromResource(this, R.array.type_users,
            R.layout.support_simple_spinner_dropdown_item)
        binding.spUsers.adapter = adapterSpinner
        binding.spUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mTypeUser = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.edtDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 8) {
                    val DNI: String = binding.edtDni.text.toString()
                    searchDni(DNI)
                }
            }
        })

        binding.btnEditDni.setOnClickListener {
            binding.edtDni.isEnabled = true
            binding.edtFullname.setText("")
            binding.edtDni.setText("")
            binding.btnEditDni.visibility = View.GONE
            binding.tilFullname.visibility = View.GONE
        }

        binding.btnRegister.setOnClickListener {
            register()
        }

    }



    private fun searchDni(dni: String) {
        showDialog("Buscando DNI...")
        val gson = GsonBuilder().serializeNulls().create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val interfaceDNI: InterfaceDNI = retrofit.create(InterfaceDNI::class.java)
        val call: Call<DNI> = interfaceDNI.getDataDni(dni + TOKEN)
        call.enqueue(object : Callback<DNI> {
            override fun onResponse(call: Call<DNI>, response: Response<DNI>) {

                if (!response.isSuccessful) {
                    Toast.makeText(this@RegisterUsersActivity, "Error !", Toast.LENGTH_SHORT)
                        .show()
                    binding.edtDni.setText("")
                    hideDialog()
                }

                else if (response.body()?.apellidoPaterno?.isNotBlank()!!) {
                    var data = ""
                    data = response.body()!!.apellidoPaterno + " " + response.body()!!
                        .apellidoMaterno + " " + response.body()!!.nombres
                    binding.tilFullname.visibility = View.VISIBLE
                    binding.btnEditDni.visibility = View.VISIBLE
                    binding.edtDni.isEnabled = false
                    binding.edtFullname.setText(data)
                    hideDialog()
                } else {
                    binding.edtDni.setText("")
                    hideDialog()
                    Toast.makeText(this@RegisterUsersActivity, "Error de DNI", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DNI>, t: Throwable) {
                binding.edtDni.setText("")
                hideDialog()
                Toast.makeText(
                    this@RegisterUsersActivity,
                    "Error!, inténtelo mas tarde",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    private fun register() {
        val email : String = binding.edtEmail.text.toString()
        val password : String =  binding.edtPassword.text.toString()
        val dni : String = binding.edtDni.text.toString()
        val fullname : String = binding.edtFullname.text.toString()

        if (dni.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty()){
            if (mTypeUser != "..."){
                showDialog("Registrando Usuario...")
                mAuth.register(email,password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val id = task.result!!.user!!.uid
                        if (mTypeUser == Constants.ADMIN){
                            val data = User(id,dni,fullname,mTypeUser,"",email,password)
                            mAdmin.create(data).addOnCompleteListener { response ->
                                if (response.isSuccessful){
                                    mAuth.logout()
                                    mAuth.login(mEmail,mPassword).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            hideDialog()
                                            finish()
                                            Toast.makeText(this, "Usuario creado!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                else{
                                    hideDialog()
                                    Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                                }
                            }.addOnCanceledListener{
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener {
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }
                        }
                        else if (mTypeUser == Constants.WORKER){
                            val data = Worker(id,dni,fullname,mTypeUser,"",email,password)
                            mWorker.create(data).addOnCompleteListener { response ->
                                if (response.isSuccessful){
                                    hideDialog()
                                    mAuth.login(mEmail,mPassword).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            hideDialog()
                                            finish()
                                            Toast.makeText(this, "Usuario creado!", Toast.LENGTH_LONG).show()
                                        }
                                    }                               }
                                else{
                                    hideDialog()
                                    Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                                }
                            }.addOnCanceledListener{
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener {
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }
                        }
                        else if (mTypeUser == Constants.DELIVERY){
                            val data = Vehicle(id,dni,fullname,mTypeUser,"",email,password)
                            mDelivery.create(data).addOnCompleteListener { response ->
                                if (response.isSuccessful){
                                    hideDialog()
                                    mAuth.login(mEmail,mPassword).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            hideDialog()
                                            finish()
                                            Toast.makeText(this, "Usuario creado!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }.addOnCanceledListener{
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener {
                                hideDialog()
                                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else{
                        hideDialog()
                        Toast.makeText(this,"Error al registrar datos!",Toast.LENGTH_SHORT).show()
                    }
                }
            } else{
                Toast.makeText(this,"Seleccione un tipo de usuario!",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"Complete todo el formulario",Toast.LENGTH_SHORT).show()
        }

    }

}