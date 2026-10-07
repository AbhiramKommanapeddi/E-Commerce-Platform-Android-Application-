package com.tutedude.ecommerce.data.repository

import com.tutedude.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Product>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun toggleFavorite(product: Product)
    suspend fun removeFavorite(productId: String)
}
