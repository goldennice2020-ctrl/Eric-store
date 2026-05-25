package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debuffs")
data class DebuffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val triggerRule: String,
    val solution: String,
    val active: Boolean,
    val orderIndex: Int
)
