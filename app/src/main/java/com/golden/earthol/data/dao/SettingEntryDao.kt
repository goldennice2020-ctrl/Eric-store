package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.SettingEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingEntryDao {
    @Query("SELECT * FROM setting_entries ORDER BY orderIndex, category, title")
    fun observeEntries(): Flow<List<SettingEntryEntity>>

    @Query("SELECT * FROM setting_entries ORDER BY orderIndex, category, title")
    suspend fun getAll(): List<SettingEntryEntity>

    @Query("SELECT * FROM setting_entries WHERE `key` = :key LIMIT 1")
    fun observeByKey(key: String): Flow<SettingEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SettingEntryEntity>)
}
