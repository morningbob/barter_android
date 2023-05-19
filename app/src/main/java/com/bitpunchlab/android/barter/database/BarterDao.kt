package com.bitpunchlab.android.barter.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.UserAndProductOffering

@Dao
interface BarterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductOffering(product: ProductOffering)

    @Query("SELECT * FROM products_offering")
    fun getAllProductOffering() : LiveData<List<ProductOffering>>

    @Query("SELECT * FROM products_offering WHERE :id == productId LIMIT 1")
    fun getProductOffering(id: String) : LiveData<ProductOffering>

    @Transaction
    @Query("SELECT * from users")
    fun getUsersAndProductsOffering() : List<UserAndProductOffering>
}