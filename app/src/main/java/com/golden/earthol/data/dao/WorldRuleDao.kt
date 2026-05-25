package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.WorldRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldRuleDao {
    @Query("SELECT * FROM world_rules ORDER BY orderIndex")
    fun observeRules(): Flow<List<WorldRuleEntity>>

    @Query("SELECT * FROM world_rules ORDER BY orderIndex")
    suspend fun getAll(): List<WorldRuleEntity>

    @Insert
    suspend fun insertAll(items: List<WorldRuleEntity>)
}
