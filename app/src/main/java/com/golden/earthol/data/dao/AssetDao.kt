package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY orderIndex")
    fun observeAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets ORDER BY orderIndex")
    suspend fun getAll(): List<AssetEntity>

    @Insert
    suspend fun insert(asset: AssetEntity)

    @Insert
    suspend fun insertAll(assets: List<AssetEntity>)

    @Delete
    suspend fun delete(asset: AssetEntity)
}
