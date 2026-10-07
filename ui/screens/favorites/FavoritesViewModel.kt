package com.tutedude.ecommerce.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.data.repository.FavoriteRepository
import com.tutedude.ecommerce.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Product> = emptyList()
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.getAllFavorites().collect { favList ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favorites = favList
                    )
                }
            }
        }
    }

    fun removeFavorite(productId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(productId)
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(product)
        }
    }
}
