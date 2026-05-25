package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_styles")
data class PlayerStyleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val focus: String,
    val buff: String,
    val debuff: String,
    val selected: Boolean,
    val guideText: String,
    val orderIndex: Int
)
