package com.bitpunchlab.android.barter.database

import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.AcceptBidAndBid
import com.bitpunchlab.android.barter.models.AcceptBidAndProduct
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.models.UserAndAcceptBid
import com.bitpunchlab.android.barter.models.UserAndBid
import com.bitpunchlab.android.barter.models.UserAndMessage
import com.bitpunchlab.android.barter.models.UserAndProductOffering
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object BarterRepository {

    var database: BarterDatabase? = null

    fun insertCurrentUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertUser(user)
        }
    }

    fun getAllUsers(): Flow<List<User>>? {
        return database?.barterDao?.getAllUsers()
    }

    fun getCurrentUser(id: String): Flow<List<User>>? {
        return database?.barterDao?.getUser(id)
    }

    fun getAllProductOffering(): Flow<List<ProductOffering>>? {
        return database?.barterDao?.getAllProductOffering()

    }

    fun getAllProductOfferingByList() : List<ProductOffering>? {
        return database?.barterDao?.getAllProductOfferingByList()
    }

    suspend fun getProductOfferingById(id: String) : ProductOffering? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductOfferingById(id)
        }.await()
    }

    fun insertProductsOffering(products: List<ProductOffering>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductsOffering(*products.toTypedArray())
        }
    }

    fun deleteProductOffering(products: List<ProductOffering>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.deleteProductOffering(*products.toTypedArray())
        }
    }

    fun insertProductsAsking(products: List<ProductAsking>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductsAsking(*products.toTypedArray())
        }
    }

    fun deleteProductsAsking(products: List<ProductAsking>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.deleteProductAsking(*products.toTypedArray())
        }
    }

    fun insertImages(images: List<ProductImageToDisplay>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertProductImages(*images.toTypedArray())
        }
    }

    suspend fun getImage(url: String) : List<ProductImageToDisplay>? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductImage(url)
        }.await()
    }
    suspend fun getUserProductsOffering(id: String): List<UserAndProductOffering>? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getUserAndProductsOffering(id)
        }.await()
    }


    suspend fun getProductOfferingWithProductsAsking(id: String):
            Flow<List<ProductOfferingAndProductsAsking>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductOfferingAndProductsAsking(id)
        }.await()

    suspend fun getProductOfferingWithBids(id: String):
            Flow<List<ProductOfferingAndBids>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getProductOfferingAndBids(id)
        }.await()

    fun insertBids(bids: List<Bid>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertBids(*bids.toTypedArray())
        }
    }

    suspend fun getAcceptBidById(id: String) : AcceptBid? {
        return CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getAcceptBidById(id)
        }.await()
    }

    suspend fun getUserAndAcceptBids(id: String): Flow<List<UserAndAcceptBid>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getUserAndAcceptBidsById(id)
        }.await()

    suspend fun getAcceptBidAndProductById(id: String) : List<AcceptBidAndProduct>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getAcceptBidAndProductById(id)
        }.await()

    suspend fun getAcceptBidAndBidById(id: String) : List<AcceptBidAndBid>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getAcceptBidAndBidById(id)
        }.await()

    suspend fun getCurrentBidsById(id: String) : Flow<List<UserAndBid>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getUserAndBidsById(id)
        }.await()

    suspend fun getUserAndMessageById(id: String) : Flow<List<UserAndMessage>>? =
        CoroutineScope(Dispatchers.IO).async {
            database?.barterDao?.getUserAndMessageById(id)
        }.await()

    fun insertMessages(messages: List<Message>) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.barterDao?.insertMessages(*messages.toTypedArray())
        }
    }
}


/*
    suspend fun retrieveAcceptedBids() : Flow<List<AcceptBid>>? {
        return CoroutineScope(Dispatchers.IO).async {
            //database?.barterDao?.getAllAcceptedBids()
        }.await()
    }

 */
