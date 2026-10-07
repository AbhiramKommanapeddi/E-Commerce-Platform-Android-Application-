package com.tutedude.ecommerce.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteProductDao {

    @Query("SELECT * FROM favorite_products ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :productId)")
    fun isFavorite(productId: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_products WHERE id = :productId LIMIT 1")
    suspend fun getFavoriteById(productId: String): FavoriteProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteProductEntity)

    @Delete
    suspend fun deleteFavorite(product: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE id = :productId")
    suspend fun deleteFavoriteById(productId: String)
}
