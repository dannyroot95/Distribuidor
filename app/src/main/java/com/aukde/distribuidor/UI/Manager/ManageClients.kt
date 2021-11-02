package com.aukde.distribuidor.UI.Manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aukde.distribuidor.Adapters.ListClientAdapter
import com.aukde.distribuidor.Adapters.ListUsersAdapter
import com.aukde.distribuidor.Models.Clients
import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Providers.ClientProvider
import com.aukde.distribuidor.UI.Registers.RegisterClient
import com.aukde.distribuidor.databinding.ActivityManageClientsBinding

class ManageClients : AppCompatActivity() {

    private lateinit var binding : ActivityManageClientsBinding
    private lateinit var mClient : ClientProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mClient = ClientProvider()

        binding.btnRegisterClients.setOnClickListener {
            startActivity(Intent(this,RegisterClient::class.java))
        }
        getClientList()
    }

    private fun getClientList() {
        mClient.getClientList(this)
    }

    /**
     * A function to get the success result of users list from firebase.
     *
     * @param clientList
     */

    fun successClientList(clientList : ArrayList<Clients>){
        if (clientList.size > 0) {
            binding.rvClients.layoutManager = LinearLayoutManager(this)
            binding.rvClients.setHasFixedSize(true)
            val usersAdapter = ListClientAdapter(this,clientList)
            binding.rvClients.adapter = usersAdapter

        }
    }

    override fun onResume() {
        super.onResume()
        getClientList()
    }

}