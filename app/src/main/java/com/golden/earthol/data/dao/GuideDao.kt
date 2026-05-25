package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.GuideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDao {
    @Query("SELECT * FROM guides ORDER BY orderIndex")
    fun observeGuides(): Flow<List<GuideEntity>>

    @Query("SELECT * FROM guides ORDER BY orderIndex")
    suspend fun getAll(): List<GuideEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GuideEntity>)

    @Query("DELETE FROM guides")
    suspend fun clear()
}
