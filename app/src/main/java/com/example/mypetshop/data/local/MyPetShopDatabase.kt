package com.example.mypetshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PetProfileEntity::class,
        ProductEntity::class,
        ClientMessageEntity::class,
        UserEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class MyPetShopDatabase : RoomDatabase() {
    abstract fun petProfileDao(): PetProfileDao
    abstract fun productDao(): ProductDao
    abstract fun clientMessageDao(): ClientMessageDao
    abstract fun userDao(): UserDao
}
