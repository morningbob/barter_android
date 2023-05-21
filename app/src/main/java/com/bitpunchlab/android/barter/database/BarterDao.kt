package com.bitpunchlab.android.barter.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
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
    suspend fun insertProductOffering(product: ProductOffering)

    @Query("SELECT * FROM products_offering")
    fun getAllProductOffering() : Flow<List<ProductOffering>>

    @Query("SELECT * FROM products_offering WHERE :id = productId LIMIT 1")
    fun getProductOffering(id: String) : Flow<ProductOffering>

    @Transaction
    @Query("SELECT * from users")
    suspend fun getUsersAndProductsOffering() : List<UserAndProductOffering>
}