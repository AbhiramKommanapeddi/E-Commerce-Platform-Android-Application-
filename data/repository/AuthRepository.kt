package com.tutedude.ecommerce.data.repository

import com.tutedude.ecommerce.domain.model.User
import com.tutedude.ecommerce.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Resource<User>>
    suspend fun register(email: String, password: String, displayName: String, phone: String): Flow<Resource<User>>
    suspend fun logout()
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
}
