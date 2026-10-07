package com.tutedude.ecommerce.data.repository

import android.net.Uri
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getUserProducts(): Flow<Resource<List<Product>>>
    fun getRecommendedProducts(): Flow<Resource<List<Product>>>
    fun getProductById(id: String): Flow<Resource<Product>>
    fun uploadProduct(product: Product, imageUris: List<Uri>): Flow<Resource<String>>
}
