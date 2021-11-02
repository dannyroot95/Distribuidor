package com.aukde.distribuidor.UI.Menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Services.ForegroundService
import com.aukde.distribuidor.UI.LoginActivity
import com.aukde.distribuidor.databinding.ActivityMenuAdminBinding

class MenuAdmin : AppCompatActivity() {

    private lateinit var binding : ActivityMenuAdminBinding
    private lateinit var mAuth : AuthenticationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = AuthenticationProvider()

        binding.btnLogout.setOnClickListener {
            mAuth.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


    override fun onBackPressed() {
    }

}