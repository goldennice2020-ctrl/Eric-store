package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.LifeStageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeStageDao {
    @Query("SELECT * FROM life_stages ORDER BY orderIndex")
    fun observeStages(): Flow<List<LifeStageEntity>>

    @Query("SELECT * FROM life_stages ORDER BY orderIndex")
    suspend fun getAll(): List<LifeStageEntity>

    @Insert
    suspend fun insertAll(items: List<LifeStageEntity>): List<Long>
}
