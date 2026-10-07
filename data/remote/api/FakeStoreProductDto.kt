package com.tutedude.ecommerce.data.remote.api

import com.google.gson.annotations.SerializedName
import com.tutedude.ecommerce.domain.model.Product

data class FakeStoreProductDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("rating")
    val rating: RatingDto? = null
) {
    data class RatingDto(
        @SerializedName("rate")
        val rate: Double = 0.0,
        @SerializedName("count")
        val count: Int = 0
    )

    fun toDomainModel(): Product {
        // FakeStore provides 1 image; we provide additional high-res variations for the minimum 3 images requirement
        val multiImages = listOf(
            image,
            "https://picsum.photos/seed/${id}_1/600/600",
            "https://picsum.photos/seed/${id}_2/600/600"
        )

        return Product(
            id = "fakestore_$id",
            title = title,
            description = description,
            price = price,
            category = category.replaceFirstChar { it.uppercase() },
            imageUrls = multiImages,
            uploaderId = "fakestore_official",
            uploaderName = "FakeStore Verified Merchant",
            uploaderEmail = "support@fakestoreapi.com",
            uploaderPhone = "+1 (800) 555-0199",
            isRecommended = true,
            rating = rating?.rate ?: 4.5,
            ratingCount = rating?.count ?: 120
        )
    }
}
