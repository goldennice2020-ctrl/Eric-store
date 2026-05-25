package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY orderIndex")
    fun observePlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places ORDER BY orderIndex")
    suspend fun getAll(): List<PlaceEntity>

    @Insert
    suspend fun insert(place: PlaceEntity)

    @Insert
    suspend fun insertAll(places: List<PlaceEntity>)

    @Delete
    suspend fun delete(place: PlaceEntity)
}
