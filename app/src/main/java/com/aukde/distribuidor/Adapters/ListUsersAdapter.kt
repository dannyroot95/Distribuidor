package com.aukde.distribuidor.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aukde.distribuidor.Models.User
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.databinding.ItemUsersLayoutBinding
import com.squareup.picasso.Picasso


open class ListUsersAdapter(private val context: Context , private var list: ArrayList<User>) : RecyclerView.Adapter<ListUsersAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemUsersLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = ItemUsersLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyViewHolder(binding)
    }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int)  {
        val model = list[position]
            if (model.perfil.isNotEmpty()){
                Picasso.with(context).load(model.perfil).into(holder.binding.itImage)
            }
            holder.binding.itName.text = model.nombre
            holder.binding.itDni.text = "DNI : "+model.dni
            holder.binding.itTypeUser.text = model.tipoUsuario

            when (model.tipoUsuario) {
                Constants.SUPER_USER -> {
                    holder.binding.itTypeUser.setTextColor(Color.parseColor("#AF0000"))
                }
                Constants.ADMIN -> {
                    holder.binding.itTypeUser.setTextColor(Color.parseColor("#00716F"))
                }
                Constants.WORKER -> {
                    holder.binding.itTypeUser.setTextColor(Color.parseColor("#6C3483"))
                }
                Constants.DELIVERY -> {
                    holder.binding.itTypeUser.setTextColor(Color.parseColor("#007113"))
                }
            }

            holder.binding.itEmail.text = model.email

                holder.itemView.setOnClickListener {
                    Toast.makeText(context,"Hello Peter",Toast.LENGTH_SHORT).show()

            }

        }

    override fun getItemCount(): Int {
        return list.size
    }

}