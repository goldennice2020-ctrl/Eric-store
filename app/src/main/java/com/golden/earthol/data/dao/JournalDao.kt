package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journals ORDER BY entryDate DESC, updatedAt DESC")
    fun observeAll(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journals ORDER BY entryDate DESC, updatedAt DESC")
    suspend fun getAll(): List<JournalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: JournalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<JournalEntity>)

    @Query("DELETE FROM journals")
    suspend fun clear()
}
