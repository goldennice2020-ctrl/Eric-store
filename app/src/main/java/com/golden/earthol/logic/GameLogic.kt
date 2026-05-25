package com.golden.earthol.logic

import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.DebuffEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity

data class TodayStrategy(
    val name: String,
    val description: String,
    val advice: String
)

object GameLogic {
    fun requiredExp(level: Int): Int = level * 100

    fun applyPlayerExp(player: PlayerEntity, gain: Int): PlayerEntity {
        var level = player.level
        var exp = player.exp + gain
        while (exp >= requiredExp(level)) {
            exp -= requiredExp(level)
            level += 1
        }
        return player.copy(level = level, exp = exp, updatedAt = System.currentTimeMillis())
    }

    fun applyAttributeExp(attribute: AttributeEntity, gain: Int): AttributeEntity {
        val total = attribute.exp + gain
        return attribute.copy(level = attribute.level + total / 100, exp = total % 100)
    }

    fun combatPower(attributes: List<AttributeEntity>, talents: List<TalentEntity>): Int =
        attributes.sumOf { it.level } + talents.filter { it.unlocked }.sumOf { it.level }

    fun judgeDebuffs(status: SurvivalStatusEntity): List<DebuffEntity> {
        val items = mutableListOf<DebuffEntity>()
        if (status.sleep < 40) items += DebuffEntity(
            name = "睡眠赤字",
            description = "判断力下降，冲动决策概率上升，任务效率下降。",
            triggerRule = "sleep < 40",
            solution = "今天不要做重大决策，优先补觉和完成低难任务。",
            active = true,
            orderIndex = 1
        )
        if (status.mental < 40) items += DebuffEntity(
            name = "精神赤字",
            description = "对未来悲观，执行力下降，社交意愿下降。",
            triggerRule = "mental < 40",
            solution = "只推进一个最小任务，不要开启新项目。",
            active = true,
            orderIndex = 2
        )
        if (status.cashPressure > 70) items += DebuffEntity(
            name = "现金流压力",
            description = "长期压力提升，高风险决策概率上升。",
            triggerRule = "cashPressure > 70",
            solution = "停止幻想型项目，优先做短路径现金流验证。",
            active = true,
            orderIndex = 3
        )
        if (status.stress > 75) items += DebuffEntity(
            name = "高压状态",
            description = "情绪波动放大，恢复速度下降。",
            triggerRule = "stress > 75",
            solution = "暂停高冲突任务，转入恢复模式。",
            active = true,
            orderIndex = 4
        )
        if (status.recovery < 40) items += DebuffEntity(
            name = "恢复不足",
            description = "连续推进能力下降，容易中途崩溃。",
            triggerRule = "recovery < 40",
            solution = "今天的主线不是突破，而是恢复。",
            active = true,
            orderIndex = 5
        )
        return items
    }

    fun todayStrategy(status: SurvivalStatusEntity): TodayStrategy = when {
        status.energy < 30 || status.mental < 30 -> TodayStrategy("危险日", "角色进入低血量区间。", "不要做重大决策，不开新项目，不冲动消费。")
        status.energy < 50 || status.mental < 50 -> TodayStrategy("恢复日", "基础状态不足，硬冲容易叠 Debuff。", "只做低阻力任务，优先恢复。")
        status.energy >= 75 && status.mental >= 75 -> TodayStrategy("猛攻日", "体力和精神都在线。", "适合推进高价值主线任务。")
        else -> TodayStrategy("推进日", "状态足够推进，但不适合多线开战。", "适合推进一个明确主线，不适合同时开太多项目。")
    }
}
