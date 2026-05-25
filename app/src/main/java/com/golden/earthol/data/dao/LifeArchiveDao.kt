package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.LifeArchiveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeArchiveDao {
    @Query("SELECT * FROM life_archives ORDER BY date DESC, createdAt DESC")
    fun observeArchives(): Flow<List<LifeArchiveEntity>>

    @Query("SELECT * FROM life_archives ORDER BY date DESC, createdAt DESC")
    suspend fun getAll(): List<LifeArchiveEntity>

    @Insert
    suspend fun insertAll(items: List<LifeArchiveEntity>)
}
