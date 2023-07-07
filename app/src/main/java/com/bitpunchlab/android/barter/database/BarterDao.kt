package com.bitpunchlab.android.barter.database

import androidx.room.*
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.AcceptBidAndBid
import com.bitpunchlab.android.barter.models.AcceptBidAndProduct
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.BidAndAcceptBid
import com.bitpunchlab.android.barter.models.ProductAndAcceptBid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.models.UserAndAcceptBid
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

    //@Query("SELECT * from products_offering WHERE :id = productOfferingId")
    //fun getAskingProducts(id: String) : Flow<List<ProductOffering>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsBidding(vararg productBidding: ProductBidding)

    @Query("SELECT * from products_bidding")
    fun getAllProductsBidding() : Flow<List<ProductBidding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBids(vararg bid: Bid)
/*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceptedBids(vararg acceptedBids: AcceptBid)

    @Query("SELECT * FROM accept_bids")
    fun getAllAcceptedBids() : Flow<List<AcceptBid>>
*/
    @Query("SELECT * FROM products_bidding WHERE :id = productOfferingForBid LIMIT 1")
    fun getProductBiddingById(id: String) : ProductBidding

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsAsking(vararg productAsking: ProductAsking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceptBids(vararg bid: AcceptBid)

    //@Query("SELECT * FROM accept_bids WHERE")
    //suspend fun getAcceptedBids(userId: String) : Flow<List<AcceptBid>>

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

    //@Query("SELECT * FROM ")

/*
    @Transaction
    @Query("SELECT * FROM products_offering")
    fun getProductAndAcceptBid() : List<ProductAndAcceptBid>

    @Transaction
    @Query("SELECT * FROM bids")
    fun getBidAndAcceptBid() : List<BidAndAcceptBid>

    @Transaction
    @Query("SELECT * FROM products_offering WHERE :id == productId")
    fun getProductAndAcceptBidById(id: String) : List<ProductAndAcceptBid>

    @Transaction
    @Query("SELECT * FROM bids WHERE :id == bidId")
    fun getBidAndAcceptBidById(id: String) : List<BidAndAcceptBid>

 */
}