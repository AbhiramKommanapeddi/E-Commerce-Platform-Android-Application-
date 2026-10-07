package com.tutedude.ecommerce.data.remote.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    // MARK: - Authentication

    val currentUser: FirebaseUser?
        get() = try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        try {
            auth.addAuthStateListener(listener)
        } catch (e: Exception) {
            trySend(null)
        }
        awaitClose {
            try {
                auth.removeAuthStateListener(listener)
            } catch (_: Exception) {}
        }
    }

    suspend fun login(email: String, password: String):Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw Exception("Login failed: empty user returned")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String, phone: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw Exception("Registration failed")

            // Store user profile in Firestore
            try {
                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "email" to (user.email ?: email),
                    "displayName" to displayName,
                    "phoneNumber" to phone,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(user.uid).set(userMap).await()
            } catch (ignored: Exception) {
                // If firestore users collection write fails or not yet provisioned, continue
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
    }

    // MARK: - Firestore Products

    fun getProductsFlow(): Flow<List<Product>> = callbackFlow {
        val collection = firestore.collection("products")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val registration = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // In case Firestore is not yet configured or security rules block, emit empty so fallback handles it
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull { doc ->
                    try {
                        Product(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            category = doc.getString("category") ?: "General",
                            imageUrls = (doc.get("imageUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            uploaderId = doc.getString("uploaderId") ?: "",
                            uploaderName = doc.getString("uploaderName") ?: "User",
                            uploaderEmail = doc.getString("uploaderEmail") ?: "",
                            uploaderPhone = doc.getString("uploaderPhone") ?: "",
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(products)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    suspend fun uploadProduct(product: Product): Result<String> {
        return try {
            val productId = product.id.ifBlank { UUID.randomUUID().toString() }
            val productMap = hashMapOf(
                "id" to productId,
                "title" to product.title,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "imageUrls" to product.imageUrls,
                "uploaderId" to product.uploaderId,
                "uploaderName" to product.uploaderName,
                "uploaderEmail" to product.uploaderEmail,
                "uploaderPhone" to product.uploaderPhone,
                "createdAt" to product.createdAt
            )
            firestore.collection("products").document(productId).set(productMap).await()
            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK: - Firebase Storage

    suspend fun uploadImage(uri: Uri, folder: String = "products"): Result<String> {
        return try {
            val filename = "${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child("$folder/$filename")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
