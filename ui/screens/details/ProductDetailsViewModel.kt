package com.tutedude.ecommerce.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.data.repository.FavoriteRepository
import com.tutedude.ecommerce.data.repository.ProductRepository
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailsUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProductDetails()
        observeFavoriteStatus()
    }

    private fun loadProductDetails() {
        viewModelScope.launch {
            productRepository.getProductById(productId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                product = resource.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            favoriteRepository.isFavorite(productId).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    fun toggleFavorite() {
        val currentProduct = _uiState.value.product ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(currentProduct)
        }
    }
}
