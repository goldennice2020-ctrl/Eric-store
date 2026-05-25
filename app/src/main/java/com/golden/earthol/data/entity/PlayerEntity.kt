package com.golden.earthol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "邱硕",
    val title: String = "现实玩家 / AI 产品 / 创造玩家",
    val level: Int = 12,
    val exp: Int = 1240,
    val cash: Int = 68000,
    val currentStageId: Long? = null,
    val currentStyleId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
