package com.bitpunchlab.android.barter.database

import androidx.room.*
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
import kotlinx.coroutines.flow.Flow

@Dao
interface BarterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers() : Flow<List<User>>

    @Query("SELECT * FROM users WHERE :id = id")
    fun getUser(id: String) : Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsOffering(vararg product: ProductOffering)

    @Delete
    suspend fun deleteProductOffering(vararg product: ProductOffering)

    @Query("SELECT * FROM products_offering WHERE :id == productId LIMIT 1")
    fun getProductOfferingById(id: String) : ProductOffering

    @Query("SELECT * FROM products_offering")
    fun getAllProductOffering() : Flow<List<ProductOffering>>

    @Query("SELECT * FROM products_offering WHERE :id = productId LIMIT 1")
    fun getProductOffering(id: String) : Flow<ProductOffering>

    @Transaction
    @Query("SELECT * from users")
    suspend fun getUsersAndProductsOffering() : List<UserAndProductOffering>

    @Transaction
    @Query("SELECT * FROM products_offering")
    suspend fun getProductsOfferingAndBids() : List<ProductOfferingAndBids>

    @Transaction
    @Query("SELECT * FROM products_offering WHERE :id = productId")
    fun getProductOfferingAndBids(id: String) : Flow<List<ProductOfferingAndBids>>

    @Transaction
    @Query("SELECT * FROM products_offering")
    fun getProductsOfferingAndProductsAsking() : List<ProductOfferingAndProductsAsking>

    @Transaction
    @Query("SELECT * from users WHERE :id = id")
    fun getUserAndProductsOffering(id: String) : List<UserAndProductOffering>

    @Transaction
    @Query("SELECT * FROM products_offering WHERE :id = productId")
    fun getProductOfferingAndProductsAsking(id: String) : Flow<List<ProductOfferingAndProductsAsking>>

    @Transaction
    @Query("SELECT * FROM products_offering WHERE :id = productId")
    fun getProductOfferingAndProductsAskingAsList(id: String) : List<ProductOfferingAndProductsAsking>

    @Transaction
    @Query("SELECT * FROM products_offering WHERE :id = productId")
    fun getProductOfferingAndBidsAsList(id: String) : List<ProductOfferingAndBids>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductImages(vararg image: ProductImageToDisplay)

    @Query("SELECT * FROM images_table WHERE :url == imageUrlCloud")
    suspend fun getProductImage(url: String) : List<ProductImageToDisplay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBids(vararg bid: Bid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsAsking(vararg productAsking: ProductAsking)

    @Delete
    suspend fun deleteProductAsking(vararg productAsking: ProductAsking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceptBids(vararg bid: AcceptBid)

    @Query("SELECT * FROM accept_bids WHERE :id == acceptId LIMIT 1")
    suspend fun getAcceptBidById(id: String) : AcceptBid

    @Transaction
    @Query("SELECT * FROM accept_bids")
    fun getAcceptBidAndProduct() : List<AcceptBidAndProduct>

    @Transaction
    @Query("SELECT * FROM accept_bids")
    fun getAcceptBidAndBid() : List<AcceptBidAndBid>

    @Transaction
    @Query("SELECT * FROM accept_bids WHERE :id == acceptId")
    fun getAcceptBidAndProductById(id: String) : List<AcceptBidAndProduct>

    @Transaction
    @Query("SELECT * FROM accept_bids WHERE :id == acceptId")
    fun getAcceptBidAndBidById(id: String) : List<AcceptBidAndBid>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUserAndAcceptBids() : List<UserAndAcceptBid>

    @Transaction
    @Query("SELECT * FROM users WHERE :id == id")
    fun getUserAndAcceptBidsById(id: String) : Flow<List<UserAndAcceptBid>>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUserAndBid() : Flow<List<UserAndBid>>

    @Transaction
    @Query("SELECT * FROM users WHERE :id == id")
    fun getUserAndBidsById(id: String) : Flow<List<UserAndBid>>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUserAndMessage() : List<UserAndMessage>

    @Transaction
    @Query("SELECT * FROM users WHERE :id == id")
    fun getUserAndMessageById(id: String) : Flow<List<UserAndMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(vararg message: Message)

    //@Query("SELECT * FROM messages")
    //fun getAllMessages

}