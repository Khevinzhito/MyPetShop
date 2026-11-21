package com.example.mypetshop.di

import com.example.mypetshop.data.repository.AuthRepositoryImpl
import com.example.mypetshop.data.repository.MessageRepositoryImpl
import com.example.mypetshop.data.repository.PetRepositoryImpl
import com.example.mypetshop.data.repository.ProductRepositoryImpl
import com.example.mypetshop.domain.repository.AuthRepository
import com.example.mypetshop.domain.repository.MessageRepository
import com.example.mypetshop.domain.repository.PetRepository
import com.example.mypetshop.domain.repository.ProductRepository
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
    abstract fun bindPetRepository(impl: PetRepositoryImpl): PetRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository
}
