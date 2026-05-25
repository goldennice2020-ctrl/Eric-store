package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golden.earthol.data.entity.TalentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TalentDao {
    @Query("SELECT * FROM talents ORDER BY orderIndex")
    fun observeTalents(): Flow<List<TalentEntity>>

    @Query("SELECT * FROM talents ORDER BY orderIndex")
    suspend fun getAll(): List<TalentEntity>

    @Insert
    suspend fun insertAll(items: List<TalentEntity>)

    @Insert
    suspend fun insert(item: TalentEntity): Long

    @Update
    suspend fun update(item: TalentEntity)

    @Delete
    suspend fun delete(item: TalentEntity)
}
