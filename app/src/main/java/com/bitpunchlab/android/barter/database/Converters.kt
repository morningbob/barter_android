package com.bitpunchlab.android.barter.database

import androidx.room.TypeConverter
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun productsOfferingFromString(productsString: String) : List<ProductOffering> {
        val objectType = object : TypeToken<List<ProductOffering>>() {}.type
        return Gson().fromJson<List<ProductOffering>>(productsString, objectType)
    }

    @TypeConverter
    fun productsOfferingToString(productsOffering: List<ProductOffering>) : String {
        return Gson().toJson(productsOffering)
    }

    @TypeConverter
    fun askingProductsFromString(productsString: String) : List<ProductAsking> {
        val objectType = object : TypeToken<List<ProductAsking>>() {}.type
        return Gson().fromJson<List<ProductAsking>>(productsString, objectType)
    }

    @TypeConverter
    fun askingProductsToString(productsAsking: List<ProductAsking>) : String {
        return Gson().toJson(productsAsking)
    }

    @TypeConverter
    fun imagesFromString(imagesString: String) : List<String> {
        val objectType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(imagesString, objectType)
        //return listOf()
    }

    @TypeConverter
    fun imagesToString(images: List<String>) : String {
        return Gson().toJson(images)
    }


}