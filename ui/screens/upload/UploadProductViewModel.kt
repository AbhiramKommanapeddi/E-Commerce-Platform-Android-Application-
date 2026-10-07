package com.tutedude.ecommerce.ui.screens.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.data.repository.AuthRepository
import com.tutedude.ecommerce.data.repository.ProductRepository
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.util.Resource
import com.tutedude.ecommerce.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class UploadProductUiState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val category: String = "Electronics",
    val imageUris: List<Uri> = emptyList(),
    val imageUrlsInput: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class UploadProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadProductUiState())
    val uiState: StateFlow<UploadProductUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc, errorMessage = null) }
    }

    fun onPriceChange(newPrice: String) {
        _uiState.update { it.copy(price = newPrice, errorMessage = null) }
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun addImageUris(newUris: List<Uri>) {
        val current = _uiState.value.imageUris.toMutableList()
        current.addAll(newUris)
        _uiState.update { it.copy(imageUris = current, errorMessage = null) }
    }

    fun removeImageUri(index: Int) {
        val current = _uiState.value.imageUris.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _uiState.update { it.copy(imageUris = current) }
        }
    }

    fun addSampleImages() {
        val randomSeed = UUID.randomUUID().toString().take(6)
        val sampleUrls = listOf(
            "https://picsum.photos/seed/${randomSeed}_1/800/800",
            "https://picsum.photos/seed/${randomSeed}_2/800/800",
            "https://picsum.photos/seed/${randomSeed}_3/800/800"
        )
        _uiState.update { it.copy(imageUrlsInput = sampleUrls, errorMessage = null) }
    }

    fun uploadProduct() {
        val state = _uiState.value
        val totalImagesCount = state.imageUris.size + state.imageUrlsInput.size

        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter product title") }
            return
        }
        val priceVal = state.price.toDoubleOrNull()
        if (priceVal == null || priceVal <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid price") }
            return
        }
        if (state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter product description") }
            return
        }
        // Minimum 3 images requirement from assignment description
        if (totalImagesCount < 3) {
            _uiState.update {
                it.copy(errorMessage = "Please add at least 3 product images (requirement: minimum 3). You can also click 'Fill Sample Photos'!")
            }
            return
        }

        val currentUser = authRepository.getCurrentUser()

        val product = Product(
            id = "user_prod_${System.currentTimeMillis()}",
            title = state.title.trim(),
            description = state.description.trim(),
            price = priceVal,
            category = state.category,
            imageUrls = if (state.imageUrlsInput.isNotEmpty()) state.imageUrlsInput else emptyList(),
            uploaderId = currentUser?.uid ?: "current_user",
            uploaderName = currentUser?.displayName ?: "MarketHub Seller",
            uploaderEmail = currentUser?.email ?: "seller@markethub.app",
            uploaderPhone = currentUser?.phoneNumber.takeUnless { it.isNullOrBlank() } ?: "+91 98765 00000",
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            productRepository.uploadProduct(product, state.imageUris).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        // Trigger local push notification (Bonus feature)
                        NotificationHelper.showProductUploadedNotification(
                            context = context,
                            productTitle = state.title,
                            price = priceVal
                        )
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Upload failed"
                            )
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { UploadProductUiState() }
    }
}
