package com.bitpunchlab.android.barter.database

import android.content.Context
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBid
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.models.UserAndProductOffering
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BarterRepository {

    var database : BarterDatabase? = null

    fun insertCurrentUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertUser(user)
        }
    }

    fun getAllUsers() : Flow<List<User>>? {
        return database?.barterDao?.getAllUsers()
    }

    fun getCurrentUser(id: String) : Flow<List<User>>? {
        return database?.barterDao?.getUser(id)
    }

    fun getAllProductOffering() : Flow<List<ProductOffering>>? {
        return database?.barterDao?.getAllProductOffering()

    }

    fun insertProductsOffering(products: List<ProductOffering>)  {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductsOffering(*products.toTypedArray())
        }
    }

    fun insertProductsAsking(products: List<ProductAsking>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductsAsking(*products.toTypedArray())
        }
    }

    suspend fun getUserProductsOffering(id: String) : List<UserAndProductOffering>? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getUserAndProductsOffering(id)
        }.await()
    }

    //suspend fun getAskingProducts(id: String) : Flow<List<ProductOffering>>? {
    //    return database?.barterDao?.getAskingProducts(id)
    //}

    fun insertProductsBidding(productsBidding: List<ProductBidding>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductsBidding(*productsBidding.toTypedArray())
        }
    }

    suspend fun getAllProductsBidding() : Flow<List<ProductBidding>>? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getAllProductsBidding()
        }.await()
    }

    suspend fun getProductOfferingWithProductsAsking(id: String) :
            Flow<List<ProductOfferingAndProductAsking>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductOfferingAndProductsAsking(id)
        }.await()

    suspend fun getProductOfferingWithBids(id: String) :
            Flow<List<ProductOfferingAndBid>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductOfferingAndBids(id)
        }.await()

    fun insertBids(bids: List<Bid>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertBids(*bids.toTypedArray())
        }
    }


/*
    suspend fun retrieveAcceptedBids() : Flow<List<AcceptBid>>? {
        return CoroutineScope(Dispatchers.IO).async {
            //database?.barterDao?.getAllAcceptedBids()
        }.await()
    }

 */
}