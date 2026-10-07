package com.tutedude.ecommerce.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val favoriteProductDao: FavoriteProductDao

    companion object {
        const val DATABASE_NAME = "ecommerce_market_hub.db"
    }
}
