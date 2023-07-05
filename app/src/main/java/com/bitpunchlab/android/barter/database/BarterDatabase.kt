package com.bitpunchlab.android.barter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
import kotlinx.coroutines.InternalCoroutinesApi


@Database(entities = [User::class, ProductOffering::class, ProductBidding::class,
                     ProductAsking::class, Bid::class, AcceptBid::class]
, version = 22, exportSchema = false)

@TypeConverters(Converters::class)
abstract class BarterDatabase : RoomDatabase() {
    abstract val barterDao: BarterDao

    companion object {
        @Volatile
        private var INSTANCE: BarterDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context?): BarterDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context!!.applicationContext,
                        BarterDatabase::class.java,
                        "barter_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}