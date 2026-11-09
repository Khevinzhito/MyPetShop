package com.example.mypetshop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetProfileDao {

    @Query("SELECT * FROM pet_profiles ORDER BY id DESC LIMIT 1")
    fun observeLatest(): Flow<PetProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PetProfileEntity): Long
}
