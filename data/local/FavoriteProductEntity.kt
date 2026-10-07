package com.tutedude.ecommerce.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tutedude.ecommerce.domain.model.Product

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrlsString: String,
    val uploaderId: String,
    val uploaderName: String,
    val uploaderEmail: String,
    val uploaderPhone: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Product {
        val urls = if (imageUrlsString.isBlank()) {
            emptyList()
        } else {
            imageUrlsString.split("||").filter { it.isNotBlank() }
        }
        return Product(
            id = id,
            title = title,
            description = description,
            price = price,
            category = category,
            imageUrls = urls,
            uploaderId = uploaderId,
            uploaderName = uploaderName,
            uploaderEmail = uploaderEmail,
            uploaderPhone = uploaderPhone,
            isFavorite = true
        )
    }

    companion object {
        fun fromDomainModel(product: Product): FavoriteProductEntity {
            return FavoriteProductEntity(
                id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                category = product.category,
                imageUrlsString = product.imageUrls.joinToString("||"),
                uploaderId = product.uploaderId,
                uploaderName = product.uploaderName,
                uploaderEmail = product.uploaderEmail,
                uploaderPhone = product.uploaderPhone
            )
        }
    }
}
