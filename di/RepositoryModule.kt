package com.tutedude.ecommerce.di

import com.tutedude.ecommerce.data.repository.AuthRepository
import com.tutedude.ecommerce.data.repository.AuthRepositoryImpl
import com.tutedude.ecommerce.data.repository.FavoriteRepository
import com.tutedude.ecommerce.data.repository.FavoriteRepositoryImpl
import com.tutedude.ecommerce.data.repository.ProductRepository
import com.tutedude.ecommerce.data.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
