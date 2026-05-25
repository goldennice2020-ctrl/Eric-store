package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val stage: String,
    val progress: Int,
    val status: String = "active",
    val nextAction: String,
    val guideText: String,
    val orderIndex: Int
)
