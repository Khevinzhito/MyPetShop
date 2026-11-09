package com.example.mypetshop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientMessageDao {

    @Query("SELECT * FROM client_messages ORDER BY sentAtMillis DESC")
    fun observeAll(): Flow<List<ClientMessageEntity>>

    @Query("SELECT * FROM client_messages WHERE clientEmail = :email ORDER BY sentAtMillis DESC")
    fun observeForClient(email: String): Flow<List<ClientMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ClientMessageEntity)
}
