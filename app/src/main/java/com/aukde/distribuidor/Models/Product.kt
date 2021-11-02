package com.aukde.distribuidor.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(val id : String = "",
                   val id_company : String = "",
                   val image : String = "",
                   val title : String = "",
                   val category : String = "",
                   val code : String = "" ,
                   val quantity : String = "",
                   val metric : String = "",
                   val unit : String = "",
                   val sub_metric : String = "",
                   val sub_unit : String = "",
                   val quantity_magnitude : String = "",
                   val magnitude : String = "",
                   val price_whole: String = "",
                   val price_pack : String = "",
                   val price_unit : String = "") : Parcelable
