package com.aukde.distribuidor.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aukde.distribuidor.Models.Product
import com.aukde.distribuidor.databinding.ItemProductLayoutBinding
import com.squareup.picasso.Picasso

class ListProductAdapter (private val context: Context, private var list: ArrayList<Product>) : RecyclerView.Adapter<ListProductAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemProductLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListProductAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        if (model.image.isNotEmpty()){
            Picasso.with(context).load(model.image).into(holder.binding.itImage)
        }
        holder.binding.itNameProduct.text = model.title
        holder.binding.itCode.text = "Código : "+model.code
        holder.binding.itPriceTotal.text = "P.total : S/"+model.price_unit
        holder.binding.itPriceUnit.text = "P.unitario : S/"+model.price_unit
        holder.binding.itStock.text = "Stock : "+model.quantity

        holder.binding.cardItem.setOnClickListener {
            Toast.makeText(context,model.id, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}