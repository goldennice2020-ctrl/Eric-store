package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golden.earthol.data.entity.DailyStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatusDao {
    @Query("SELECT * FROM daily_status WHERE date = :date")
    fun observeByDate(date: String): Flow<DailyStatusEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(status: DailyStatusEntity)
}
