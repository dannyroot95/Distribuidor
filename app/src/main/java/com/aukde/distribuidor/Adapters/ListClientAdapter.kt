package com.aukde.distribuidor.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aukde.distribuidor.Models.Clients
import com.aukde.distribuidor.databinding.ItemClientLayoutBinding

class ListClientAdapter (private val context: Context, private var list: ArrayList<Clients>) : RecyclerView.Adapter<ListClientAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemClientLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListClientAdapter.MyViewHolder {
        val binding = ItemClientLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListClientAdapter.MyViewHolder, position: Int) {
         val model = list[position]
         holder.binding.itName.text = model.nombre
         holder.binding.itAddress.text = model.direccion

         holder.binding.lyItem.setOnClickListener {
             Toast.makeText(context,model.id, Toast.LENGTH_SHORT).show()
         }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}