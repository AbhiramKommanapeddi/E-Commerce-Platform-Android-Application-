package com.tutedude.ecommerce.ui.screens.home

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

data class HomeUiState(
    val isLoading: Boolean = false,
    val userProducts: List<Product> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val selectedCategory: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeFavorites()
    }

    fun loadData() {
        loadUserProducts()
        loadRecommendedProducts()
    }

    private fun loadUserProducts() {
        viewModelScope.launch {
            productRepository.getUserProducts().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                userProducts = resource.data ?: emptyList()
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

    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            productRepository.getRecommendedProducts().collect { resource ->
                if (resource is Resource.Success) {
                    _uiState.update {
                        it.copy(recommendedProducts = resource.data ?: emptyList())
                    }
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.getAllFavorites().collect { favorites ->
                val favIds = favorites.map { it.id }.toSet()
                _uiState.update { it.copy(favoriteProductIds = favIds) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(product)
        }
    }

    /**
     * Filters products based on current search query and category filter
     */
    fun getFilteredUserProducts(): List<Product> {
        val state = _uiState.value
        return state.userProducts.filter { product ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    product.title.contains(state.searchQuery, ignoreCase = true) ||
                    product.description.contains(state.searchQuery, ignoreCase = true)

            val matchesCategory = state.selectedCategory.isBlank() ||
                    state.selectedCategory.equals("All", ignoreCase = true) ||
                    product.category.contains(state.selectedCategory, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }
}
