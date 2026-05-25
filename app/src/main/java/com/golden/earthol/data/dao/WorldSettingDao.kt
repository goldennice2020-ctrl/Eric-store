package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.WorldSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldSettingDao {
    @Query("SELECT * FROM world_settings ORDER BY category, title")
    fun observeAll(): Flow<List<WorldSettingEntity>>

    @Query("SELECT * FROM world_settings ORDER BY category, title")
    suspend fun getAll(): List<WorldSettingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WorldSettingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WorldSettingEntity>)

    @Query("DELETE FROM world_settings")
    suspend fun clear()
}
