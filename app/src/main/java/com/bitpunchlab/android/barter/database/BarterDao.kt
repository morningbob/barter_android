package com.bitpunchlab.android.barter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bitpunchlab.android.barter.models.ProductOffering
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface BarterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductOffering(product: ProductOffering)

    @Query("SELECT * FROM products_offering")
    fun getAllProductOffering() : MutableStateFlow<List<ProductOffering>>

    @Query("SELECT * FROM products_offering WHERE :id == id LIMIT 1")
    fun getProductOffering(id: String) : MutableStateFlow<ProductOffering>


}