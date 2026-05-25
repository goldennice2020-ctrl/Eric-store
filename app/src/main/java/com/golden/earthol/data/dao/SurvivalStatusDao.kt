package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golden.earthol.data.entity.SurvivalStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurvivalStatusDao {
    @Query("SELECT * FROM survival_status ORDER BY date DESC, id DESC LIMIT 1")
    fun observeLatest(): Flow<SurvivalStatusEntity?>

    @Query("SELECT * FROM survival_status ORDER BY date DESC, id DESC LIMIT 1")
    suspend fun getLatest(): SurvivalStatusEntity?

    @Query("SELECT * FROM survival_status ORDER BY date DESC, id DESC")
    suspend fun getAll(): List<SurvivalStatusEntity>

    @Insert
    suspend fun insert(status: SurvivalStatusEntity)

    @Insert
    suspend fun insertAll(items: List<SurvivalStatusEntity>)

    @Update
    suspend fun update(status: SurvivalStatusEntity)
}
