package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guides")
data class GuideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val category: String,
    val content: String,
    val relatedModule: String,
    val orderIndex: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
