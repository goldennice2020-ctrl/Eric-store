package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golden.earthol.data.entity.AttributeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributeDao {
    @Query("SELECT * FROM attributes ORDER BY orderIndex")
    fun observeAttributes(): Flow<List<AttributeEntity>>

    @Query("SELECT * FROM attributes ORDER BY orderIndex")
    suspend fun getAll(): List<AttributeEntity>

    @Query("SELECT * FROM attributes WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): AttributeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AttributeEntity>)

    @Update
    suspend fun update(item: AttributeEntity)

    @Query("DELETE FROM attributes")
    suspend fun clear()
}
