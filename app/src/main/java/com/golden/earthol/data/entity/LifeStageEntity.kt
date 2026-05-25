package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_stages")
data class LifeStageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val mainGoal: String,
    val warning: String,
    val nextAction: String,
    val current: Boolean,
    val guideText: String,
    val orderIndex: Int
)
