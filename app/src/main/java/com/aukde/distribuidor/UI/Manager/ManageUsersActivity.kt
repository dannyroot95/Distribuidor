package com.aukde.distribuidor.UI.Manager

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aukde.distribuidor.Adapters.ListUsersAdapter
import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Providers.SuperUserProvider
import com.aukde.distribuidor.UI.Registers.RegisterUsersActivity
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.databinding.ActivityManageUsersBinding


class ManageUsersActivity : BaseActivity() {

    private lateinit var binding : ActivityManageUsersBinding
    private lateinit var mUser : SuperUserProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUser = SuperUserProvider()

        binding.btnRegisterUsers.setOnClickListener {
            startActivity(Intent(this, RegisterUsersActivity::class.java))
        }
        getUserList()
    }

    private fun getUserList(){
        mUser.getUserList(this@ManageUsersActivity)
    }

    /**
     * A function to get the success result of users list from firebase.
     *
     * @param userList
     */

    fun successUsersList(userList : ArrayList<User>){
        if (userList.size > 0) {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
            binding.rvUsers.setHasFixedSize(true)

            val usersAdapter = ListUsersAdapter(this,userList)
            binding.rvUsers.adapter = usersAdapter

        }
    }

    override fun onResume() {
        super.onResume()
        getUserList()
    }

}