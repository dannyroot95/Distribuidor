package com.aukde.distribuidor.UI.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Providers.SuperUserProvider
import com.aukde.distribuidor.UI.LoginActivity
import com.aukde.distribuidor.UI.Manager.ManageProductActivity
import com.aukde.distribuidor.UI.Manager.ManageUsersActivity
import com.aukde.distribuidor.UI.Maps.MapClientActivity
import com.aukde.distribuidor.UI.Maps.MapWorkerActivity
import com.aukde.distribuidor.databinding.ActivityMenuSuperAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MenuSuperAdmin : AppCompatActivity() {

    private lateinit var binding : ActivityMenuSuperAdminBinding
    private lateinit var mAuth : AuthenticationProvider
    private lateinit var mSudo : SuperUserProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuSuperAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = AuthenticationProvider()
        mSudo = SuperUserProvider()

        binding.btnLogout.setOnClickListener {
            mAuth.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
        binding.btnManageProducts.setOnClickListener {
            startActivity(Intent(this, ManageProductActivity::class.java))
        }
        binding.btnMapWorkers.setOnClickListener {
            startActivity(Intent(this, MapWorkerActivity::class.java))
        }
        binding.btnMapClients.setOnClickListener {
            startActivity(Intent(this, MapClientActivity::class.java))
        }
        getData()
    }

    private fun getData(){
        mSudo.getData(mAuth.getId()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val name = snapshot.child("nombre").value.toString()
                    binding.tvFullname.text = name
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
    }

}