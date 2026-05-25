package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val efficiencyScore: Int,
    val recoveryScore: Int,
    val opportunityScore: Int,
    val costLevel: String,
    val buff: String,
    val debuff: String,
    val notes: String = "",
    val guideText: String,
    val orderIndex: Int
)
