package com.tutedude.ecommerce.data.repository

import com.tutedude.ecommerce.data.local.FavoriteProductDao
import com.tutedude.ecommerce.data.local.FavoriteProductEntity
import com.tutedude.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteProductDao: FavoriteProductDao
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<Product>> {
        return favoriteProductDao.getAllFavorites().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun isFavorite(productId: String): Flow<Boolean> {
        return favoriteProductDao.isFavorite(productId)
    }

    override suspend fun toggleFavorite(product: Product) {
        val existing = favoriteProductDao.getFavoriteById(product.id)
        if (existing != null) {
            favoriteProductDao.deleteFavoriteById(product.id)
        } else {
            favoriteProductDao.insertFavorite(FavoriteProductEntity.fromDomainModel(product))
        }
    }

    override suspend fun removeFavorite(productId: String) {
        favoriteProductDao.deleteFavoriteById(productId)
    }
}
