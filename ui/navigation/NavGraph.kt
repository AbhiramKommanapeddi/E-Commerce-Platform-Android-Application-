package com.tutedude.ecommerce.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tutedude.ecommerce.ui.screens.auth.AuthViewModel
import com.tutedude.ecommerce.ui.screens.auth.LoginScreen
import com.tutedude.ecommerce.ui.screens.auth.RegisterScreen
import com.tutedude.ecommerce.ui.screens.details.ProductDetailsScreen
import com.tutedude.ecommerce.ui.screens.details.ProductDetailsViewModel
import com.tutedude.ecommerce.ui.screens.favorites.FavoritesScreen
import com.tutedude.ecommerce.ui.screens.favorites.FavoritesViewModel
import com.tutedude.ecommerce.ui.screens.home.HomeScreen
import com.tutedude.ecommerce.ui.screens.home.HomeViewModel
import com.tutedude.ecommerce.ui.screens.profile.ProfileScreen
import com.tutedude.ecommerce.ui.screens.upload.UploadProductScreen
import com.tutedude.ecommerce.ui.screens.upload.UploadProductViewModel
import com.tutedude.ecommerce.ui.theme.Indigo600

@Composable
fun MainAppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
        BottomNavItem.Upload,
        BottomNavItem.Profile
    )

    // Show bottom navigation only on primary main tabs
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Favorites.route,
        Screen.Upload.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    tonalElevation = 6.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Indigo600,
                                selectedTextColor = Indigo600,
                                indicatorColor = Indigo600.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState.isSuccess) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Register Screen
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home Screen
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDetails = { productId ->
                        navController.navigate(Screen.Details.createRoute(productId))
                    },
                    onNavigateToUpload = {
                        navController.navigate(Screen.Upload.route)
                    }
                )
            }

            // Product Details Screen
            composable(Screen.Details.route) {
                val detailsViewModel: ProductDetailsViewModel = hiltViewModel()
                ProductDetailsScreen(
                    viewModel = detailsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Upload Product Screen
            composable(Screen.Upload.route) {
                val uploadViewModel: UploadProductViewModel = hiltViewModel()
                UploadProductScreen(
                    viewModel = uploadViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onUploadSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Favorites Screen (Room Database)
            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onNavigateToDetails = { productId ->
                        navController.navigate(Screen.Details.createRoute(productId))
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            // Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    user = authState.user,
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
