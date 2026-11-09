package com.example.mypetshop.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Room
import com.example.mypetshop.data.local.ClientMessageDao
import com.example.mypetshop.data.local.UserDao
import com.example.mypetshop.data.local.MyPetShopDatabase
import com.example.mypetshop.data.local.PetProfileDao
import com.example.mypetshop.data.local.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyPetShopDatabase {
        val builder = Room.databaseBuilder(context, MyPetShopDatabase::class.java, "mypetshop.db")
        if (context.isDebuggable()) {
            builder.fallbackToDestructiveMigration(dropAllTables = true)
        }
        return builder.build()
    }

    @Provides
    fun providePetProfileDao(database: MyPetShopDatabase): PetProfileDao =
        database.petProfileDao()

    @Provides
    fun provideProductDao(database: MyPetShopDatabase): ProductDao =
        database.productDao()

    @Provides
    fun provideUserDao(database: MyPetShopDatabase): UserDao =
        database.userDao()

    @Provides
    fun provideClientMessageDao(database: MyPetShopDatabase): ClientMessageDao =
        database.clientMessageDao()
}

private fun Context.isDebuggable(): Boolean =
    (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
