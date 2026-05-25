package com.golden.earthol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.golden.earthol.data.entity.LibraryContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryContentDao {
    @Query("SELECT * FROM library_contents ORDER BY updatedAt DESC, id DESC")
    fun observeContents(): Flow<List<LibraryContentEntity>>

    @Query("SELECT * FROM library_contents ORDER BY updatedAt DESC, id DESC")
    suspend fun getAll(): List<LibraryContentEntity>

    @Insert
    suspend fun insert(content: LibraryContentEntity)
}
