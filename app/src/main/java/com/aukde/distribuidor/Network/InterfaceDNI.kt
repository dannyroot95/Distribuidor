package com.aukde.distribuidor.Network

import com.aukde.distribuidor.Models.DNI
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface InterfaceDNI {
    @GET
    fun getDataDni(@Url url: String): Call<DNI>
}