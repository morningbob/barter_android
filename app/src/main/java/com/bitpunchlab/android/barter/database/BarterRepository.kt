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

    //var context : Context? = null

    //val _productsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    //val productsOffering : StateFlow<List<ProductOffering>> get() = _productsOffering.asStateFlow()

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
}