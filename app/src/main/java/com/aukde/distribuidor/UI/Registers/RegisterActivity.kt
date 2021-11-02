package com.aukde.distribuidor.UI.Registers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Models.DNI
import com.aukde.distribuidor.Network.InterfaceDNI
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Providers.SuperUserProvider
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.databinding.ActivityRegisterBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : BaseActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var mAuth : AuthenticationProvider
    private lateinit var mSuperAdmin : SuperUserProvider

    var URL = "https://dniruc.apisperu.com/api/v1/dni/"
    var BASE_TOKEN = "?token="
    var TOKEN = BASE_TOKEN + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNhbWFuLmRhbm55OTVAZ21haWwuY29tIn0.J7apbfAgC6PK_L9EJBkJWMdJmHZZxYWVr2HFEp8WqLQ"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = AuthenticationProvider()
        mSuperAdmin = SuperUserProvider()

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
                    Toast.makeText(this@RegisterActivity, "Error !", Toast.LENGTH_SHORT)
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
                    Toast.makeText(this@RegisterActivity, "Error de DNI", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DNI>, t: Throwable) {
                binding.edtDni.setText("")
                hideDialog()
                Toast.makeText(
                    this@RegisterActivity,
                    "Error!, inténtelo mas tarde",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun register(){

        val email : String = binding.edtEmail.text.toString()
        val password : String =  binding.edtPassword.text.toString()
        val dni : String = binding.edtDni.text.toString()
        val fullname : String = binding.edtFullname.text.toString()
        val authPass : String = binding.edtAuthPass.text.toString()
        val type = Constants.SUPER_USER

        if (dni.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && authPass.isNotEmpty() && fullname.isNotEmpty()){
            if (authPass == "AUK"){
                showDialog("Registrando Usuario...")
                mAuth.register(email,password).addOnCompleteListener {  task ->
                    if (task.isSuccessful){
                        val id = task.result!!.user!!.uid
                        val superAdmin = User(id,dni,fullname,type,"",email,password)
                        mSuperAdmin.create(superAdmin).addOnCompleteListener { response ->
                            if (response.isSuccessful) {
                                Toast.makeText(this,"Usuario creado!",Toast.LENGTH_SHORT).show()
                                hideDialog()
                                mAuth.logout()
                                finish()
                            }else{
                                hideDialog()
                                Toast.makeText(this,"Error al registrar datos!",Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            hideDialog()
                            Toast.makeText(this,"Error al registrar datos!",Toast.LENGTH_SHORT).show()
                        }.addOnCanceledListener {
                            hideDialog()
                            Toast.makeText(this,"Error al registrar datos!",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        hideDialog()
                        Toast.makeText(this,"Error al registrar usuario!",Toast.LENGTH_SHORT).show()
                    }
                }.addOnCanceledListener {
                    hideDialog()
                    Toast.makeText(this,"Error al registrar usuario!",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this,"Error al registrar usuario!",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Clave de autorización INVÁLIDA!",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            hideDialog()
            Toast.makeText(this,"Complete todo el formulario",Toast.LENGTH_SHORT).show()
        }

    }

}