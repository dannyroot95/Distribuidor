package com.aukde.distribuidor.UI.Manager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.aukde.distribuidor.Adapters.ListProductAdapter
import com.aukde.distribuidor.Models.Product
import com.aukde.distribuidor.Providers.ProductProvider
import com.aukde.distribuidor.UI.Registers.ProductActivity
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.databinding.ActivityManageProductBinding
import kotlinx.android.synthetic.main.activity_manage_product.view.*
import java.util.*
import kotlin.collections.ArrayList

class ManageProductActivity : BaseActivity() {

    private lateinit var binding : ActivityManageProductBinding
    private var mProduct : ProductProvider = ProductProvider()
    private lateinit var itemList: ArrayList<Product>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegisterProduct.setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
        }
        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String): Boolean {
                searching(text)
                return true
            }
        })
        getProductList()
    }

    private fun searching(text: String) {
        val list : ArrayList<Product> = ArrayList<Product>()
        for (newProduct in itemList){
            if (newProduct.title.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) {
                list.add(newProduct)
            }
            else{}
        }
        val productAdapter = ListProductAdapter(this,list)
        binding.rvProducts.adapter = productAdapter
    }

    private fun getProductList(){
        mProduct.geProductList(this)
    }

    fun successProductList(productList : ArrayList<Product>){
        if (productList.size > 0) {
            itemList = productList
            binding.rvProducts.layoutManager = GridLayoutManager(this,3)
            binding.rvProducts.setHasFixedSize(true)

            val productAdapter = ListProductAdapter(this,productList)
            binding.rvProducts.adapter = productAdapter

        }
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

}