package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.HiddenQuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenQuestDao {
    @Query("SELECT * FROM hidden_quests ORDER BY orderIndex")
    fun observeHiddenQuests(): Flow<List<HiddenQuestEntity>>

    @Query("SELECT * FROM hidden_quests ORDER BY orderIndex")
    suspend fun getAll(): List<HiddenQuestEntity>

    @Insert
    suspend fun insertAll(items: List<HiddenQuestEntity>)
}
