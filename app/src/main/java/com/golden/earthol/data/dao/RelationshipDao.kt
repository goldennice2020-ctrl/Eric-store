package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.RelationshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDao {
    @Query("SELECT * FROM relationships ORDER BY updatedAt DESC, id DESC")
    fun observeAll(): Flow<List<RelationshipEntity>>

    @Query("SELECT * FROM relationships ORDER BY updatedAt DESC, id DESC")
    suspend fun getAll(): List<RelationshipEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: RelationshipEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RelationshipEntity>)

    @Delete
    suspend fun delete(item: RelationshipEntity)

    @Query("DELETE FROM relationships")
    suspend fun clear()
}
