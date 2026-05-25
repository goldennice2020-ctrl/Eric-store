package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "random_events")
data class RandomEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val eventType: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val effectA: String,
    val effectB: String,
    val effectC: String,
    val resolved: Boolean,
    val guideText: String,
    val createdAt: Long = System.currentTimeMillis()
)
