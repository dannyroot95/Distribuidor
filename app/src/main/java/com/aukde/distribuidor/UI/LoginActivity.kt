package com.aukde.distribuidor.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.UI.Menu.MenuAdmin
import com.aukde.distribuidor.UI.Menu.MenuSuperAdmin
import com.aukde.distribuidor.UI.Menu.WorkerMenu
import com.aukde.distribuidor.UI.Registers.RegisterActivity
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : BaseActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mAuth : AuthenticationProvider


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

         mAuth = AuthenticationProvider()

         binding.tvRegister.setOnClickListener{
             startActivity(Intent(this, RegisterActivity::class.java))
         }

         binding.btnLogin.setOnClickListener {
             login()
         }

    }

    private fun login() {

        val email : String = binding.edtEmail.text.toString()
        val password : String =  binding.edtPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            showDialog("Iniciando Sesión...")
            mAuth.login(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val id = task.result!!.user!!.uid
                    mAuth.verifyTypeUser(id).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                val type = snapshot.child("tipoUsuario").value.toString()
                                val sharedPreferences = getSharedPreferences(Constants.TYPE_USER, Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                val sharedPreferencesEmail = getSharedPreferences(Constants.EMAIL, Context.MODE_PRIVATE)
                                val editorEmail = sharedPreferencesEmail.edit()
                                val sharedPreferencesPassword = getSharedPreferences(Constants.PASSWORD, Context.MODE_PRIVATE)
                                val editorPassword = sharedPreferencesPassword.edit()
                                if (type == Constants.SUPER_USER){
                                    hideDialog()
                                    editor.putString(Constants.KEY,Constants.SUPER_USER)
                                    editor.apply()
                                    editorEmail.putString(Constants.KEY_EMAIL,email)
                                    editorEmail.apply()
                                    editorPassword.putString(Constants.KEY_PASSWORD,password)
                                    editorPassword.apply()
                                    val intent = Intent(this@LoginActivity, MenuSuperAdmin::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                }
                                else if(type == Constants.ADMIN){
                                    hideDialog()
                                    editor.putString(Constants.KEY,Constants.ADMIN)
                                    editor.apply()
                                    val intent = Intent(this@LoginActivity, MenuAdmin::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                }
                                else if(type == Constants.WORKER){
                                    hideDialog()
                                    editor.putString(Constants.KEY,Constants.WORKER)
                                    editor.apply()
                                    val intent = Intent(this@LoginActivity, WorkerMenu::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                }
                                else if(type == Constants.DELIVERY){}
                            }
                            else{
                                mAuth.logout()
                                hideDialog()
                                Toast.makeText(this@LoginActivity, "Error!", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            mAuth.logout()
                            hideDialog()
                            Toast.makeText(this@LoginActivity, "Error!", Toast.LENGTH_LONG).show()
                        }
                    })
                }
                else{
                    hideDialog()
                    Toast.makeText(this, "Error!, revise sus credenciales", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_LONG).show()
        }

    }

    override fun onStart() {
        super.onStart()
         val preference : SharedPreferences = getSharedPreferences(Constants.TYPE_USER,MODE_PRIVATE)
         val type = preference.getString(Constants.KEY,"").toString()

        if (mAuth.existSession() && type == Constants.SUPER_USER){
            startActivity(Intent(this,MenuSuperAdmin::class.java))
        }
        else if (mAuth.existSession() && type == Constants.ADMIN){
            startActivity(Intent(this,MenuSuperAdmin::class.java))
        }
        else if (mAuth.existSession() && type == Constants.WORKER){
            startActivity(Intent(this,WorkerMenu::class.java))
        }
    }

    override fun onBackPressed() {
    }

}