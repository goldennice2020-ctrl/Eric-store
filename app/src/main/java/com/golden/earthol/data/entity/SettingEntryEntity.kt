package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setting_entries")
data class SettingEntryEntity(
    @PrimaryKey val key: String,
    val title: String,
    val category: String,
    val content: String,
    val orderIndex: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
