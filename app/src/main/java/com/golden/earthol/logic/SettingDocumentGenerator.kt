package com.golden.earthol.logic

import com.golden.earthol.data.SettingDocumentSnapshot
import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.DebuffEntity
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.LifeArchiveEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.WorldRuleEntity

enum class SettingDocumentFormat(val extension: String, val mimeType: String) {
    Markdown("md", "text/markdown"),
    Txt("txt", "text/plain")
}

object SettingDocumentGenerator {
    const val title = "地球OL完整设定文档"

    fun generateCurrentSettingDocument(
        snapshot: SettingDocumentSnapshot,
        format: SettingDocumentFormat = SettingDocumentFormat.Markdown
    ): String {
        val markdown = buildMarkdown(snapshot)
        return if (format == SettingDocumentFormat.Markdown) markdown else markdownToPlainText(markdown)
    }

    private fun buildMarkdown(snapshot: SettingDocumentSnapshot): String = buildString {
        appendLine("# $title")
        appendLine()
        appendLine("- 生成时间：${ContentConverter.nowText()}")
        appendLine("- 数据来源：当前 Room 数据库实时快照")
        appendLine("- 生成原则：App 当前是什么样，导出的文档就是什么样。")
        appendLine()

        section("一、世界观设定")
        appendSetting(snapshot, "world_view", "世界观")
        appendLibraryByCategories(snapshot, "世界观", "用户新增世界观")

        section("二、核心价值观")
        appendSetting(snapshot, "core_values", "价值观")
        appendLibraryByCategories(snapshot, "价值观", "长期理念", "理念")

        section("三、游戏核心理念")
        appendSetting(snapshot, "core_idea", "核心理念")

        section("四、地球OL规则")
        snapshot.worldRules.groupBy { it.category }.forEach { (category, rules) ->
            appendLine("### $category")
            rules.forEach { rule ->
                appendLine("#### ${rule.title}")
                appendLine(rule.content)
                appendLine()
            }
        }
        appendLibraryByCategories(snapshot, "规则", "隐藏规则", "用户新增规则")

        section("五、总攻略")
        snapshot.guides.filter { it.category == "总攻略" }.forEach { appendGuide(it) }

        section("六、各模块攻略")
        snapshot.guides.filter { it.category != "总攻略" }.groupBy { it.category }.forEach { (category, guides) ->
            appendLine("### $category")
            guides.forEach { appendGuide(it) }
        }

        section("七、核心系统")
        systemSubsection("属性系统", "属性决定玩家长期能力结构。属性等级、经验、类别和说明来自当前 attributes 表。")
        snapshot.attributes.forEach { appendLine("- ${it.name}：Lv.${it.level}，EXP ${it.exp}，${it.category}。${it.description}") }
        appendLine()

        systemSubsection("状态系统", "状态系统读取当前 survival_status 最新记录。")
        snapshot.survivalStatus?.let { status ->
            appendLine("- 日期：${status.date}")
            appendLine("- 总结：${status.summary}")
            appendLine("- 能量 ${status.energy} / 精神 ${status.mental} / 饥饿 ${status.hunger} / 睡眠 ${status.sleep} / 压力 ${status.stress} / 专注 ${status.focus} / 恢复 ${status.recovery} / 现金流压力 ${status.cashPressure} / 关系能量 ${status.relationshipEnergy}")
        } ?: appendLine("当前没有状态记录。")
        appendLine()

        systemSubsection("任务系统", "任务系统包含主线、日常、生存、Boss、隐藏任务和完成状态。")
        snapshot.tasks.groupBy { it.type }.forEach { (type, tasks) ->
            appendLine("#### $type")
            tasks.forEach { appendTask(it) }
            appendLine()
        }

        systemSubsection("关系系统", settingContent(snapshot, "relationship_system"))
        systemSubsection("金钱系统", settingContent(snapshot, "money_system"))
        snapshot.player?.let { appendLine("- 当前现金：${it.cash}") }
        snapshot.assets.filter { it.type == "cash" }.forEach { appendLine("- ${it.name}：${it.notes}，维护成本 ${it.maintenanceCost}") }
        appendLine()

        systemSubsection("运气系统", settingContent(snapshot, "luck_system"))
        snapshot.randomEvents.forEach { appendRandomEvent(it) }
        appendLine()

        systemSubsection("时间系统", settingContent(snapshot, "time_system"))
        systemSubsection("地图系统", "地图系统读取当前 places 表，每个地点都包含效率、恢复、机会、成本、Buff、Debuff、备注和攻略。")
        snapshot.places.forEach { appendPlace(it) }
        appendLine()

        systemSubsection("身体状态系统", settingContent(snapshot, "body_system"))
        systemSubsection("情绪系统", settingContent(snapshot, "emotion_system"))
        systemSubsection("成就系统", settingContent(snapshot, "achievement_system"))

        section("八、人生阶段系统")
        snapshot.lifeStages.forEach { appendLifeStage(it) }

        section("九、文明与阶层理解")
        appendSetting(snapshot, "civilization_class_understanding", "世界理解")
        appendLibraryByCategories(snapshot, "文明", "阶层", "城市观察", "职场认知")

        section("十、Buff/Debuff系统")
        snapshot.playerStyles.forEach { style ->
            appendLine("### 流派：${style.name}${if (style.selected) "（当前）" else ""}")
            appendLine(style.description)
            appendLine("- Focus：${style.focus}")
            appendLine("- Buff：${style.buff}")
            appendLine("- Debuff：${style.debuff}")
            appendLine("- 攻略：${style.guideText}")
            appendLine()
        }
        snapshot.debuffs.forEach { debuff ->
            appendLine("### Debuff：${debuff.name}${if (debuff.active) "（生效中）" else ""}")
            appendLine(debuff.description)
            appendLine("- 触发：${debuff.triggerRule}")
            appendLine("- 解法：${debuff.solution}")
            appendLine()
        }

        section("十一、用户新增设定")
        appendLibraryByCategories(snapshot, "设定", "系统", "世界观", "价值观", "规则", fallbackTitle = "当前没有用户新增设定。")

        section("十二、用户长期理念")
        appendLibraryByCategories(snapshot, "长期理念", "理念", "复盘", "经验包", fallbackTitle = "当前没有用户长期理念。")

        section("十三、用户所有长期文本内容")
        if (snapshot.libraryContents.isEmpty()) {
            appendLine("当前没有资料库内容。")
            appendLine()
        } else {
            snapshot.libraryContents.groupBy { it.category }.forEach { (category, contents) ->
                appendLine("### $category")
                contents.forEach { appendLibraryContent(it) }
            }
        }

        section("十四、模块字段说明")
        FieldCatalog.modules.forEach { module ->
            appendLine("### ${module.name}")
            appendLine(module.description)
            module.fields.forEach { appendLine("- `${it.name}`：${it.description}") }
            appendLine()
        }

        section("十五、系统参数解释")
        appendSystemParameters(snapshot)

        section("十六、历史设定汇总")
        snapshot.lifeArchives.forEach { archive ->
            appendLine("### ${archive.date} / ${archive.title}")
            appendLine("- 类型：${archive.type}")
            appendLine("- 情绪：${archive.emotionScore}")
            appendLine("- 重要性：${archive.importanceScore}")
            appendLine(archive.content)
            appendLine()
        }

        section("十七、App 当前所有可导出的文本数据")
        appendAllExportableText(snapshot)
    }.trimEnd() + "\n"

    private fun StringBuilder.section(title: String) {
        appendLine("## $title")
        appendLine()
    }

    private fun StringBuilder.systemSubsection(title: String, description: String) {
        appendLine("### $title")
        appendLine(description)
        appendLine()
    }

    private fun StringBuilder.appendSetting(snapshot: SettingDocumentSnapshot, key: String, category: String) {
        val matched = snapshot.settingEntries.firstOrNull { it.key == key }
            ?: snapshot.settingEntries.firstOrNull { it.category == category }
        appendLine(matched?.content.orEmpty().ifBlank { "当前没有${category}内容。" })
        appendLine()
    }

    private fun settingContent(snapshot: SettingDocumentSnapshot, key: String): String =
        snapshot.settingEntries.firstOrNull { it.key == key }?.content.orEmpty()

    private fun StringBuilder.appendGuide(guide: GuideEntity) {
        appendLine("### ${guide.title}")
        if (guide.subtitle.isNotBlank()) appendLine(guide.subtitle)
        appendLine("- 分类：${guide.category}")
        appendLine("- 模块：${guide.relatedModule}")
        appendLine(guide.content)
        appendLine()
    }

    private fun StringBuilder.appendTask(task: TaskEntity) {
        appendLine("- [${task.status}] ${task.title}")
        appendLine("  - 说明：${task.description}")
        appendLine("  - 奖励：EXP ${task.expReward}${task.attributeName?.let { "，$it +${task.attributeReward}" }.orEmpty()}")
        task.dueDate?.let { appendLine("  - 截止：$it") }
        if (task.bossMaxHp != null) appendLine("  - Boss HP：${task.bossCurrentHp ?: 0}/${task.bossMaxHp}")
        appendLine("  - 攻略：${task.guideText}")
    }

    private fun StringBuilder.appendRandomEvent(event: RandomEventEntity) {
        appendLine("#### ${event.eventType} / ${event.title}")
        appendLine(event.description)
        appendLine("- A ${event.optionA}：${event.effectA}")
        appendLine("- B ${event.optionB}：${event.effectB}")
        appendLine("- C ${event.optionC}：${event.effectC}")
        appendLine("- 状态：${if (event.resolved) "已处理" else "未处理"}")
        appendLine("- 攻略：${event.guideText}")
    }

    private fun StringBuilder.appendPlace(place: PlaceEntity) {
        appendLine("#### ${place.type} / ${place.name}")
        appendLine("- 效率：${place.efficiencyScore}")
        appendLine("- 恢复：${place.recoveryScore}")
        appendLine("- 机会：${place.opportunityScore}")
        appendLine("- 成本：${place.costLevel}")
        appendLine("- Buff：${place.buff}")
        appendLine("- Debuff：${place.debuff}")
        if (place.notes.isNotBlank()) appendLine("- 备注：${place.notes}")
        appendLine("- 攻略：${place.guideText}")
    }

    private fun StringBuilder.appendLifeStage(stage: LifeStageEntity) {
        appendLine("### ${stage.name}${if (stage.current) "（当前阶段）" else ""}")
        appendLine(stage.description)
        appendLine("- 主目标：${stage.mainGoal}")
        appendLine("- 警告：${stage.warning}")
        appendLine("- 下一步：${stage.nextAction}")
        appendLine("- 攻略：${stage.guideText}")
        appendLine()
    }

    private fun StringBuilder.appendLibraryByCategories(
        snapshot: SettingDocumentSnapshot,
        vararg categories: String,
        fallbackTitle: String = ""
    ) {
        val keywords = categories.map { it.lowercase() }
        val matched = snapshot.libraryContents.filter { content ->
            val haystack = listOf(content.category, content.tags, content.title).joinToString(" ").lowercase()
            keywords.any { haystack.contains(it) }
        }
        if (matched.isEmpty()) {
            if (fallbackTitle.isNotBlank()) {
                appendLine(fallbackTitle)
                appendLine()
            }
            return
        }
        matched.forEach { appendLibraryContent(it) }
    }

    private fun StringBuilder.appendLibraryContent(content: LibraryContentEntity) {
        appendLine("#### ${content.title}")
        appendLine("- 分类：${content.category}")
        appendLine("- 标签：${content.tags}")
        appendLine("- 重要性：${content.importance}")
        appendLine("- pairId：${content.pairId}")
        appendLine("- sourceType：${content.sourceType}")
        appendLine("- updatedAt：${content.updatedAt}")
        appendLine(content.rawText?.takeIf { it.isNotBlank() } ?: content.readableText.orEmpty())
        if (!content.structuredJson.isNullOrBlank()) {
            appendLine()
            appendLine("```json")
            appendLine(content.structuredJson)
            appendLine("```")
        }
        appendLine()
    }

    private fun StringBuilder.appendSystemParameters(snapshot: SettingDocumentSnapshot) {
        appendLine("- 玩家等级/经验：Lv.${snapshot.player?.level ?: 1} / EXP ${snapshot.player?.exp ?: 0}")
        appendLine("- 现金：${snapshot.player?.cash ?: 0}")
        snapshot.survivalStatus?.let {
            appendLine("- 生存状态取值：能量、精神、饥饿、睡眠、压力、专注、恢复、现金流压力、关系能量。当前值来自最新日期 ${it.date}。")
        }
        appendLine("- 属性经验：每个属性包含 level 与 exp，用于表达长期成长。")
        appendLine("- 任务奖励：expReward、attributeReward、Boss HP 用于驱动成长反馈。")
        appendLine("- 地图评分：效率、恢复、机会为 0-100 的相对评分。")
        appendLine("- 资料库重要性：1-5，表示该长期文本对世界数据库的权重。")
        appendLine()
    }

    private fun StringBuilder.appendAllExportableText(snapshot: SettingDocumentSnapshot) {
        appendLine("### 设定条目")
        snapshot.settingEntries.forEach { appendLine("- ${it.category} / ${it.title}：${it.content}") }
        appendLine()
        appendLine("### 世界规则")
        snapshot.worldRules.forEach { appendLine("- ${it.category} / ${it.title}：${it.content}") }
        appendLine()
        appendLine("### 攻略")
        snapshot.guides.forEach { appendLine("- ${it.category} / ${it.title}：${it.content}") }
        appendLine()
        appendLine("### 项目")
        snapshot.projects.forEach { appendLine("- ${it.name}：${it.description}；下一步：${it.nextAction}；攻略：${it.guideText}") }
        appendLine()
        appendLine("### 资产")
        snapshot.assets.forEach { appendLine("- ${it.name}：${it.notes}；下一步：${it.nextAction}；攻略：${it.guideText}") }
        appendLine()
        appendLine("### 隐藏任务")
        snapshot.hiddenQuests.forEach { appendLine("- ${it.title}：${it.description}；触发：${it.triggerCondition}；奖励：${it.rewardText}；攻略：${it.guideText}") }
        appendLine()
        appendLine("### 人生记录")
        snapshot.lifeArchives.forEach { appendLine("- ${it.date} / ${it.title}：${it.content}") }
        appendLine()
        appendLine("### 资料库")
        snapshot.libraryContents.forEach { appendLine("- ${it.category} / ${it.title}：${it.rawText ?: it.readableText.orEmpty()}") }
        appendLine()
    }

    private fun markdownToPlainText(markdown: String): String =
        markdown
            .replace(Regex("(?m)^#{1,6}\\s*"), "")
            .replace("```json", "")
            .replace("```", "")
            .replace(Regex("`([^`]*)`"), "$1")
}

private data class FieldModule(val name: String, val description: String, val fields: List<FieldDefinition>)
private data class FieldDefinition(val name: String, val description: String)

private object FieldCatalog {
    val modules = listOf(
        module("玩家", "玩家核心身份、等级、经验、现金与当前阶段。", listOf("name", "title", "level", "exp", "cash", "currentStageId", "currentStyleId")),
        module("设定条目", "世界观、价值观、系统说明、文明理解等统一设定源。", listOf("key", "title", "category", "content", "orderIndex", "updatedAt")),
        module("世界规则", "地球 OL 规则、隐藏机制和世界理解。", fieldsOf<WorldRuleEntity>()),
        module("攻略", "总攻略和各模块攻略。", fieldsOf<GuideEntity>()),
        module("属性", "属性系统的等级、经验、分类和说明。", fieldsOf<AttributeEntity>()),
        module("状态", "最新生存状态和身体/情绪相关参数。", fieldsOf<SurvivalStatusEntity>()),
        module("天赋", "玩家天赋树、等级、解锁状态和父节点。", fieldsOf<TalentEntity>()),
        module("任务", "主线、日常、生存、Boss 等任务。", fieldsOf<TaskEntity>()),
        module("项目", "长期项目资产和推进状态。", fieldsOf<ProjectEntity>()),
        module("资产", "现金、设备、能力、项目等资产。", fieldsOf<AssetEntity>()),
        module("地图", "地点容器、Buff、Debuff 和评分。", fieldsOf<PlaceEntity>()),
        module("人生阶段", "当前阶段、主目标、警告和下一步。", fieldsOf<LifeStageEntity>()),
        module("玩家流派", "玩家 Build、Buff、Debuff 和打法。", fieldsOf<PlayerStyleEntity>()),
        module("隐藏任务", "隐藏规则、触发条件、奖励和状态。", fieldsOf<HiddenQuestEntity>()),
        module("随机事件", "运气系统中的随机事件、选项和结果。", fieldsOf<RandomEventEntity>()),
        module("人生档案", "长期记录、情绪评分和重要性。", fieldsOf<LifeArchiveEntity>()),
        module("资料库", "用户新增世界观、价值观、攻略、长期理念和所有长期文本。", fieldsOf<LibraryContentEntity>()),
        module("Buff/Debuff", "当前生效和潜在的状态负面效果。", fieldsOf<DebuffEntity>())
    )

    private fun module(name: String, description: String, fields: List<String>) =
        FieldModule(name, description, fields.map { FieldDefinition(it, describe(it)) })

    private inline fun <reified T> fieldsOf(): List<String> =
        T::class.java.declaredFields.filter { !it.isSynthetic }.map { it.name }

    private fun describe(name: String): String = when (name) {
        "id" -> "数据库主键。"
        "key" -> "稳定配置键，用于页面和导出定位设定。"
        "title" -> "标题。"
        "name" -> "名称。"
        "category" -> "分类，用于分组和路由到导出章节。"
        "content" -> "正文内容。"
        "description" -> "说明文本。"
        "summary" -> "摘要或当前状态总结。"
        "guideText" -> "模块内攻略文本。"
        "orderIndex" -> "展示与导出排序。"
        "updatedAt" -> "最近更新时间。"
        "createdAt" -> "创建时间。"
        "level" -> "等级。"
        "exp" -> "经验值。"
        "status" -> "当前状态。"
        "type" -> "类型。"
        "tags" -> "标签。"
        "rawText" -> "用户导入或记录的原始文本。"
        "structuredJson" -> "用户导入内容对应的结构化 JSON。"
        "readableText" -> "可读化文本。"
        "importance" -> "重要性权重。"
        "checksum" -> "内容校验值。"
        else -> "当前模块字段。"
    }
}
