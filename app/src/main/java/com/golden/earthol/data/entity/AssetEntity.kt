package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val valueScore: Int,
    val potentialScore: Int,
    val maintenanceCost: String,
    val nextAction: String,
    val notes: String = "",
    val guideText: String,
    val orderIndex: Int
)
