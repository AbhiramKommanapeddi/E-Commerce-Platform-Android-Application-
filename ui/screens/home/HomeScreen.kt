package com.tutedude.ecommerce.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.ui.components.*
import com.tutedude.ecommerce.ui.theme.Coral500
import com.tutedude.ecommerce.ui.theme.Indigo600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredProducts = viewModel.getFilteredUserProducts()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = null,
                            tint = Indigo600,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MarketHub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = Indigo600,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Sell Item") },
                text = { Text("Sell Product", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search Bar
            item {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChange
                )
            }

            // Category Filter Chips
            item {
                CategorySelector(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::onCategoryChange
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recommended Products Section (from FakeStore API via Retrofit)
            if (uiState.recommendedProducts.isNotEmpty() && uiState.searchQuery.isBlank()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalOffer,
                                    contentDescription = null,
                                    tint = Coral500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recommended via FakeStore API",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(uiState.recommendedProducts) { product ->
                                val isFav = uiState.favoriteProductIds.contains(product.id)
                                ProductCard(
                                    product = product,
                                    isFavorite = isFav,
                                    onProductClick = { onNavigateToDetails(product.id) },
                                    onFavoriteToggle = viewModel::toggleFavorite,
                                    modifier = Modifier.width(220.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // Section Header: User Uploaded Products
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "User Uploaded Products",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredProducts.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Loading Indicator
            if (uiState.isLoading && filteredProducts.isEmpty()) {
                item {
                    LoadingView(message = "Loading marketplace products...")
                }
            } else if (filteredProducts.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Products Found",
                        description = if (uiState.searchQuery.isNotBlank())
                            "No products match '${uiState.searchQuery}'. Try another search."
                        else
                            "No products uploaded in this category yet. Be the first to sell!",
                        actionButtonText = "Sell a Product",
                        onActionClick = onNavigateToUpload
                    )
                }
            } else {
                // List of User Uploaded Products
                items(filteredProducts) { product ->
                    val isFav = uiState.favoriteProductIds.contains(product.id)
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        ProductCard(
                            product = product,
                            isFavorite = isFav,
                            onProductClick = { onNavigateToDetails(product.id) },
                            onFavoriteToggle = viewModel::toggleFavorite
                        )
                    }
                }
            }
        }
    }
}
