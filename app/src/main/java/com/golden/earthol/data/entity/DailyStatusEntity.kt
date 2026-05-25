package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_status")
data class DailyStatusEntity(
    @PrimaryKey val date: String,
    val stamina: Int,
    val spirit: Int,
    val hunger: Int,
    val stress: Int,
    val focus: Int,
    val createdAt: Long = System.currentTimeMillis()
)
