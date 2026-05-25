package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.DebuffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebuffDao {
    @Query("SELECT * FROM debuffs ORDER BY orderIndex")
    fun observeDebuffs(): Flow<List<DebuffEntity>>

    @Query("SELECT * FROM debuffs ORDER BY orderIndex")
    suspend fun getAll(): List<DebuffEntity>

    @Insert
    suspend fun insertAll(items: List<DebuffEntity>)
}
