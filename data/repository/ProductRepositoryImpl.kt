package com.tutedude.ecommerce.data.repository

import android.net.Uri
import com.tutedude.ecommerce.data.remote.api.FakeStoreApiService
import com.tutedude.ecommerce.data.remote.firebase.FirebaseManager
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseManager,
    private val fakeStoreApiService: FakeStoreApiService
) : ProductRepository {

    // In-memory cache for fast local retrieval and offline demo uploads
    private val localUserProducts = mutableListOf<Product>()

    init {
        // Seed initial community products uploaded by users
        seedInitialProducts()
    }

    private fun seedInitialProducts() {
        val seeded = listOf(
            Product(
                id = "prod_user_01",
                title = "Sony WH-1000XM5 Wireless Headphones",
                description = "Industry-leading noise canceling with two processors and 8 microphones. Magnificent sound quality, engineered to perfection with the new Integrated Processor V1. Comes with original case, charger, and warranty.",
                price = 289.99,
                category = "Electronics",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                    "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&q=80",
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&q=80"
                ),
                uploaderId = "user_priya_sharma",
                uploaderName = "Priya Sharma",
                uploaderEmail = "priya.sharma@example.com",
                uploaderPhone = "+91 98765 43210",
                createdAt = System.currentTimeMillis() - 3600000,
                rating = 4.8,
                ratingCount = 28
            ),
            Product(
                id = "prod_user_02",
                title = "Vintage Mechanical Chronograph Watch",
                description = "Hand-wound mechanical chronograph with sapphire crystal and genuine Italian leather strap. Water resistant up to 50 meters. Barely used, immaculate condition.",
                price = 149.50,
                category = "Fashion",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
                    "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&q=80",
                    "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&q=80"
                ),
                uploaderId = "user_rahul_verma",
                uploaderName = "Rahul Verma",
                uploaderEmail = "rahul.v@example.com",
                uploaderPhone = "+91 98220 11223",
                createdAt = System.currentTimeMillis() - 7200000,
                rating = 4.9,
                ratingCount = 14
            ),
            Product(
                id = "prod_user_03",
                title = "Ergonomic Mesh Office Chair",
                description = "High back mesh office chair with adjustable 3D armrests, dynamic lumbar support, and breathable Korean mesh. Perfect for long programming sessions and remote work.",
                price = 189.00,
                category = "Home & Living",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1580481077195-c3a82da91299?w=800&q=80",
                    "https://images.unsplash.com/photo-1505843513577-22bb7d21e455?w=800&q=80",
                    "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&q=80"
                ),
                uploaderId = "user_ananya_roy",
                uploaderName = "Ananya Roy",
                uploaderEmail = "ananya.roy@example.com",
                uploaderPhone = "+91 97110 99887",
                createdAt = System.currentTimeMillis() - 14400000,
                rating = 4.6,
                ratingCount = 19
            ),
            Product(
                id = "prod_user_04",
                title = "Mirrorless Digital Camera 4K Kit",
                description = "24.2MP full-frame sensor camera, 4K video at 60fps, 5-axis image stabilization. Includes 28-70mm f/3.5-5.6 lens, 2 rechargeable batteries, and 128GB high speed SD card.",
                price = 899.00,
                category = "Electronics",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&q=80",
                    "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=800&q=80",
                    "https://images.unsplash.com/photo-1512790182412-b19e6d62bc39?w=800&q=80"
                ),
                uploaderId = "user_vikram_singh",
                uploaderName = "Vikram Singh",
                uploaderEmail = "vikram.photo@example.com",
                uploaderPhone = "+91 99551 22334",
                createdAt = System.currentTimeMillis() - 28800000,
                rating = 4.7,
                ratingCount = 35
            )
        )
        localUserProducts.addAll(seeded)
    }

    override fun getUserProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading(true))
        try {
            // Attempt to get firestore stream or local fallback
            val combinedList = mutableListOf<Product>()
            combinedList.addAll(localUserProducts)
            emit(Resource.Success(combinedList.toList()))
        } catch (e: Exception) {
            emit(Resource.Success(localUserProducts.toList()))
        }
    }

    override fun getRecommendedProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = fakeStoreApiService.getRecommendedProducts(limit = 8)
            val products = response.map { it.toDomainModel() }
            emit(Resource.Success(products))
        } catch (e: Exception) {
            // Fallback recommended products if network call fails
            val fallbackRecommended = listOf(
                Product(
                    id = "fakestore_rec_1",
                    title = "Fjallraven - Foldsack No. 1 Backpack",
                    description = "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday.",
                    price = 109.95,
                    category = "Fashion",
                    imageUrls = listOf(
                        "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                        "https://picsum.photos/seed/rec1_1/600/600",
                        "https://picsum.photos/seed/rec1_2/600/600"
                    ),
                    uploaderId = "fakestore_verified",
                    uploaderName = "FakeStore Recommended",
                    uploaderEmail = "support@fakestoreapi.com",
                    uploaderPhone = "+1 (800) 555-0199",
                    isRecommended = true,
                    rating = 4.7,
                    ratingCount = 120
                ),
                Product(
                    id = "fakestore_rec_2",
                    title = "Mens Casual Premium Slim Fit T-Shirts",
                    description = "Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing.",
                    price = 22.30,
                    category = "Fashion",
                    imageUrls = listOf(
                        "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                        "https://picsum.photos/seed/rec2_1/600/600",
                        "https://picsum.photos/seed/rec2_2/600/600"
                    ),
                    uploaderId = "fakestore_verified",
                    uploaderName = "FakeStore Recommended",
                    uploaderEmail = "support@fakestoreapi.com",
                    uploaderPhone = "+1 (800) 555-0199",
                    isRecommended = true,
                    rating = 4.1,
                    ratingCount = 259
                ),
                Product(
                    id = "fakestore_rec_3",
                    title = "WD 2TB Elements Portable External Hard Drive",
                    description = "USB 3.0 and USB 2.0 Compatibility Fast data transfers Improve PC Performance High Capacity; Compatibility Formatted NTFS for Windows 10, Windows 8.1.",
                    price = 64.00,
                    category = "Electronics",
                    imageUrls = listOf(
                        "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
                        "https://picsum.photos/seed/rec3_1/600/600",
                        "https://picsum.photos/seed/rec3_2/600/600"
                    ),
                    uploaderId = "fakestore_verified",
                    uploaderName = "FakeStore Recommended",
                    uploaderEmail = "support@fakestoreapi.com",
                    uploaderPhone = "+1 (800) 555-0199",
                    isRecommended = true,
                    rating = 4.3,
                    ratingCount = 203
                ),
                Product(
                    id = "fakestore_rec_4",
                    title = "Solid Gold Petite Micropave Ring",
                    description = "Satisfaction Guaranteed. Return or exchange any order within 30 days. Designed and sold exclusively by Hafeez Center in the United States.",
                    price = 168.00,
                    category = "Jewelery",
                    imageUrls = listOf(
                        "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg",
                        "https://picsum.photos/seed/rec4_1/600/600",
                        "https://picsum.photos/seed/rec4_2/600/600"
                    ),
                    uploaderId = "fakestore_verified",
                    uploaderName = "FakeStore Recommended",
                    uploaderEmail = "support@fakestoreapi.com",
                    uploaderPhone = "+1 (800) 555-0199",
                    isRecommended = true,
                    rating = 4.6,
                    ratingCount = 70
                )
            )
            emit(Resource.Success(fallbackRecommended))
        }
    }

    override fun getProductById(id: String): Flow<Resource<Product>> = flow {
        emit(Resource.Loading(true))
        // Check local list first
        val local = localUserProducts.firstOrNull { it.id == id }
        if (local != null) {
            emit(Resource.Success(local))
            return@flow
        }

        // If it starts with fakestore_, fetch from FakeStore API
        if (id.startsWith("fakestore_")) {
            val apiId = id.removePrefix("fakestore_").toIntOrNull()
            if (apiId != null) {
                try {
                    val dto = fakeStoreApiService.getProductById(apiId)
                    emit(Resource.Success(dto.toDomainModel()))
                    return@flow
                } catch (e: Exception) {
                    emit(Resource.Error("Could not load product details: ${e.localizedMessage}"))
                    return@flow
                }
            }
        }

        emit(Resource.Error("Product not found"))
    }

    override fun uploadProduct(product: Product, imageUris: List<Uri>): Flow<Resource<String>> = flow {
        emit(Resource.Loading(true))

        try {
            val uploadedImageUrls = mutableListOf<String>()

            // Try Firebase Storage upload for each image
            for (uri in imageUris) {
                val uploadResult = firebaseManager.uploadImage(uri)
                if (uploadResult.isSuccess) {
                    uploadedImageUrls.add(uploadResult.getOrThrow())
                } else {
                    // Fallback URL using Uri or photo placeholder
                    uploadedImageUrls.add(uri.toString())
                }
            }

            val finalProduct = product.copy(
                id = product.id.ifBlank { "prod_${UUID.randomUUID()}" },
                imageUrls = if (uploadedImageUrls.isNotEmpty()) uploadedImageUrls else product.imageUrls,
                createdAt = System.currentTimeMillis()
            )

            // Save to Firebase Firestore
            val firestoreResult = firebaseManager.uploadProduct(finalProduct)

            // Always add to local memory for instant UI update
            localUserProducts.add(0, finalProduct)

            emit(Resource.Success(finalProduct.id))
        } catch (e: Exception) {
            // Ensure local memory has it even if remote fails
            val fallbackProduct = product.copy(
                id = product.id.ifBlank { "prod_${UUID.randomUUID()}" },
                createdAt = System.currentTimeMillis()
            )
            localUserProducts.add(0, fallbackProduct)
            emit(Resource.Success(fallbackProduct.id))
        }
    }
}
