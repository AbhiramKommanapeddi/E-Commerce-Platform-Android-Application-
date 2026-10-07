package com.tutedude.ecommerce.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FakeStoreApiService {

    @GET("products")
    suspend fun getRecommendedProducts(
        @Query("limit") limit: Int = 10
    ): List<FakeStoreProductDto>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): FakeStoreProductDto

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): List<FakeStoreProductDto>

    companion object {
        const val BASE_URL = "https://fakestoreapi.com/"
    }
}
