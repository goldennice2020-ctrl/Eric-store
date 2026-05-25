package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "talents")
data class TalentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val level: Int,
    val exp: Int,
    val description: String,
    val unlocked: Boolean,
    val parentId: Long? = null,
    val orderIndex: Int
)
