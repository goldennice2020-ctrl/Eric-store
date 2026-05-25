package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.PlayerStyleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStyleDao {
    @Query("SELECT * FROM player_styles ORDER BY orderIndex")
    fun observeStyles(): Flow<List<PlayerStyleEntity>>

    @Query("SELECT * FROM player_styles ORDER BY orderIndex")
    suspend fun getAll(): List<PlayerStyleEntity>

    @Insert
    suspend fun insertAll(items: List<PlayerStyleEntity>): List<Long>
}
