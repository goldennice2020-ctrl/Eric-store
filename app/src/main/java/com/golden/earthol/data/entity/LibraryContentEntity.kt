package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_contents")
data class LibraryContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pairId: String,
    val title: String,
    val category: String,
    val tags: String,
    val importance: Int,
    val rawText: String?,
    val structuredJson: String?,
    val readableText: String?,
    val sourceType: String,
    val createdAt: String,
    val updatedAt: String,
    val version: Int = 1,
    val checksum: String
)
