package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_quests")
data class HiddenQuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val triggerCondition: String,
    val status: String,
    val rewardText: String,
    val guideText: String,
    val orderIndex: Int
)
