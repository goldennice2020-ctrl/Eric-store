package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attributes")
data class AttributeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val level: Int = 1,
    val exp: Int = 0,
    val description: String,
    val category: String,
    val orderIndex: Int
)
