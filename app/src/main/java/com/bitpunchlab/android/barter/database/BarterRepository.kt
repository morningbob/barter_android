package com.bitpunchlab.android.barter.database

import android.content.Context
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object BarterRepository {

    fun insertCurrentUser(user: User, database: BarterDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            database.barterDao.insertUser(user)
        }
    }

    fun getAllUsers(database: BarterDatabase) : Flow<List<User>> {
        return database.barterDao.getAllUsers()
    }

    fun getCurrentUser(id: String, database: BarterDatabase) : Flow<List<User>> {
        return database.barterDao.getUser(id)
    }

    fun getAllProductOffering(database: BarterDatabase) : Flow<List<ProductOffering>> {
        return database.barterDao.getAllProductOffering()

    }

    fun insertProductsOffering(database: BarterDatabase, products: List<ProductOffering>)  {
        CoroutineScope(Dispatchers.IO).launch {
            database.barterDao.insertProductsOffering(*products.toTypedArray())
        }
    }


}