package com.aukde.distribuidor.UI.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Providers.SuperUserProvider
import com.aukde.distribuidor.Providers.WorkerProvider
import com.aukde.distribuidor.R
import com.aukde.distribuidor.Services.ForegroundService
import com.aukde.distribuidor.UI.LoginActivity
import com.aukde.distribuidor.UI.Manager.ManageClients
import com.aukde.distribuidor.databinding.ActivityWorkerMenuBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class WorkerMenu : AppCompatActivity() {

    private lateinit var binding : ActivityWorkerMenuBinding
    private lateinit var mAuth : AuthenticationProvider
    private lateinit var mWorker : WorkerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_menu)
        binding = ActivityWorkerMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = AuthenticationProvider()
        mWorker = WorkerProvider()
        binding.btnLogout.setOnClickListener {
            mAuth.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        binding.btnManageClients.setOnClickListener {
            startActivity(Intent(this,ManageClients::class.java))
        }
        binding.btnManageOrders.setOnClickListener {

        }
        getData()
        startService()
    }

    private fun getData(){
        mWorker.getWorker(mAuth.getId()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val name = snapshot.child("nombre").value.toString()
                    binding.tvFullname.text = name
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onBackPressed() {
    }

}