package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.RandomEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomEventDao {
    @Query("SELECT * FROM random_events ORDER BY createdAt DESC")
    fun observeEvents(): Flow<List<RandomEventEntity>>

    @Query("SELECT * FROM random_events ORDER BY createdAt DESC")
    suspend fun getAll(): List<RandomEventEntity>

    @Insert
    suspend fun insertAll(items: List<RandomEventEntity>)
}
