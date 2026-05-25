package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survival_status")
data class SurvivalStatusEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val energy: Int,
    val mental: Int,
    val hunger: Int,
    val sleep: Int,
    val stress: Int,
    val focus: Int,
    val recovery: Int,
    val cashPressure: Int,
    val relationshipEnergy: Int,
    val date: String,
    val summary: String
)
