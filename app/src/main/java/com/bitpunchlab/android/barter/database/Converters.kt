package com.bitpunchlab.android.barter.database

import androidx.room.TypeConverter
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.Message
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
    fun MessagesFromString(messagesString: String) : List<Message> {
        val objectType = object : TypeToken<List<Message>>() {}.type
        return Gson().fromJson<List<Message>>(messagesString, objectType)
    }

    @TypeConverter
    fun MessagesToString(messages: List<Message>) : String {
        return Gson().toJson(messages)
    }

    @TypeConverter
    fun imagesFromString(imagesString: String) : List<String> {
        val objectType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(imagesString, objectType)
    }

    @TypeConverter
    fun imagesToString(images: List<String>) : String {
        return Gson().toJson(images)
    }

    @TypeConverter
    fun bidToString(bid: List<Bid>) : String {
        return Gson().toJson(bid)
    }

    @TypeConverter
    fun bidFromString(bidString: String) : List<Bid> {
        val objectType = object : TypeToken<List<Bid>>() {}.type
        return Gson().fromJson<List<Bid>>(bidString, objectType)
    }
}
/*
    fun askingProductFromString(askingString: String) : ProductAsking {
        return Gson().fromJson(askingString, ProductAsking::class.java)
    }

    fun askingProductToString(asking: ProductAsking) : String {
        return Gson().toJson(asking)
    }

    @TypeConverter
    fun askingProductFromString(askingString: String) : ProductAsking {
        val objectType = object : TypeToken<ProductAsking>() {}.type
        return Gson().fromJson<ProductAsking>(askingString, objectType)
    }

     */