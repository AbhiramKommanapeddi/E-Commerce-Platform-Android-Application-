package com.tutedude.ecommerce.domain.model

/**
 * Domain model representing a product in the marketplace.
 * Supports multiple images (minimum 3 as requested in the assignment),
 * uploader details, and local favorite status.
 */
data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "General",
    val imageUrls: List<String> = emptyList(),
    val uploaderId: String = "",
    val uploaderName: String = "",
    val uploaderEmail: String = "",
    val uploaderPhone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isRecommended: Boolean = false,
    val rating: Double = 4.5,
    val ratingCount: Int = 10
) {
    /**
     * Helper to return primary image or placeholder
     */
    val primaryImageUrl: String
        get() = imageUrls.firstOrNull() ?: "https://picsum.photos/400/400"
}
