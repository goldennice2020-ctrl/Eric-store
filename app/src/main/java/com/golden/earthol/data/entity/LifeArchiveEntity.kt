package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_archives")
data class LifeArchiveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val type: String,
    val date: String,
    val emotionScore: Int,
    val importanceScore: Int,
    val createdAt: Long = System.currentTimeMillis()
)
