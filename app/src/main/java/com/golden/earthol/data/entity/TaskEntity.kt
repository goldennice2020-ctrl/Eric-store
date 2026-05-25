package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val type: String,
    val status: String = "todo",
    val expReward: Int,
    val attributeName: String? = null,
    val attributeReward: Int = 0,
    val projectId: Long? = null,
    val bossMaxHp: Int? = null,
    val bossCurrentHp: Int? = null,
    val dueDate: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val guideText: String
)
