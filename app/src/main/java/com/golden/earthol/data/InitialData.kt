package com.golden.earthol.data

import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.AiMemoryEntity
import com.golden.earthol.data.entity.EventEntity
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.InventoryEntity
import com.golden.earthol.data.entity.JournalEntity
import com.golden.earthol.data.entity.LifeArchiveEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.RelationshipEntity
import com.golden.earthol.data.entity.SettingEntryEntity
import com.golden.earthol.data.entity.SkillEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.WorldSettingEntity
import com.golden.earthol.data.entity.WorldRuleEntity
import java.time.LocalDate

object InitialData {
    const val WORLD_VIEW = """
欢迎来到地球 OL。

这是一个没有明确主线、没有完整新手教程、出生点随机、初始资源差异巨大、NPC 智能很高但稳定性一般、随机事件频繁刷新、很多任务没有进度条、很多奖励无法量化的开放世界。

这里没有唯一正确玩法。

有人选择冲榜。
有人选择探索。
有人选择稳定生活。
有人选择关系与陪伴。
有人选择创造。
有人选择隐居。
有人选择重开。

你不需要成为全服最强玩家。
你需要找到真正适合自己的玩法流派。

地球 OL 的核心不是通关，而是：

活下去。
成为自己。
和世界建立真实连接。
最后确认：

我来过。
我见过。
我做过想做的事。
我还有留念。
但没有太多挂念。
"""

    val settingEntries = listOf(
        SettingEntryEntity(
            key = "world_view",
            title = "世界观设定",
            category = "世界观",
            content = WORLD_VIEW.trim(),
            orderIndex = 1
        ),
        SettingEntryEntity(
            key = "core_values",
            title = "核心价值观",
            category = "价值观",
            content = "地球 OL 的价值观不是冲榜崇拜，而是活下去、成为自己、建立真实连接、长期经营、减少遗憾。系统鼓励玩家把身体、情绪、现金流、关系和创造力都看成同一套人生系统，而不是只追逐单一指标。",
            orderIndex = 2
        ),
        SettingEntryEntity(
            key = "core_idea",
            title = "游戏核心理念",
            category = "核心理念",
            content = "把现实人生当成可观察、可复盘、可迭代的开放世界。任务不是为了制造焦虑，属性不是为了比较，攻略不是为了替代选择，而是帮助玩家在混乱现实中看见当前阶段、资源、限制、机会和下一步行动。",
            orderIndex = 3
        ),
        SettingEntryEntity(
            key = "relationship_system",
            title = "人际关系系统",
            category = "核心系统",
            content = "关系不是装饰支线，而是恢复、信息、机会、合作和长期支持的来源。有效连接能产生级联放大，无效连接会消耗精神值。关系系统关注真实连接、边界感、沟通质量和恢复能力。",
            orderIndex = 20
        ),
        SettingEntryEntity(
            key = "money_system",
            title = "金钱系统",
            category = "核心系统",
            content = "现金流是生存底盘。金钱系统关注现金储备、现金流压力、资产质量、项目潜力和维护成本。现金不是唯一目标，但现金流赤字会显著影响判断、情绪和行动选择。",
            orderIndex = 21
        ),
        SettingEntryEntity(
            key = "time_system",
            title = "时间系统",
            category = "核心系统",
            content = "时间是不可逆资源。日常任务、主线任务、阶段目标和长期项目都需要占用时间槽。系统鼓励减少多线开战，把每日能量集中到一个真正推进现实的动作上。",
            orderIndex = 22
        ),
        SettingEntryEntity(
            key = "luck_system",
            title = "运气系统",
            category = "核心系统",
            content = "运气表现为随机事件、机会窗口、遇见的人、身体波动和外部环境变化。玩家不能控制刷新，但可以通过现金、身体、情绪、技能和连接保留余量，提高接住随机事件的概率。",
            orderIndex = 23
        ),
        SettingEntryEntity(
            key = "body_system",
            title = "身体状态系统",
            category = "核心系统",
            content = "身体值、睡眠、饥饿、恢复和压力决定角色能否稳定推进高阶任务。身体状态不是低级需求，而是所有高级玩法的底层 Buff。",
            orderIndex = 24
        ),
        SettingEntryEntity(
            key = "emotion_system",
            title = "情绪系统",
            category = "核心系统",
            content = "情绪系统记录精神值、压力、情绪稳定和人生档案中的情绪评分。情绪不是敌人，而是系统反馈。长期忽略情绪会叠加慢性 Debuff。",
            orderIndex = 25
        ),
        SettingEntryEntity(
            key = "achievement_system",
            title = "成就系统",
            category = "核心系统",
            content = "成就不是外部奖杯，而是玩家真正完成过、体验过、创造过、连接过的证据。人生档案、项目闭环、Boss 任务和长期记录共同构成成就系统。",
            orderIndex = 26
        ),
        SettingEntryEntity(
            key = "civilization_class_understanding",
            title = "文明与阶层理解",
            category = "世界理解",
            content = "文明、阶层、城市、平台和组织都是容器。个体成长既取决于努力，也取决于所在容器提供的资源、规则、连接和上限。理解这些不是为了犬儒，而是为了减少无效挣扎并主动换地图。",
            orderIndex = 30
        )
    )

    val events: List<EventEntity>
        get() = randomEvents.map {
            EventEntity(
                title = it.title,
                description = it.description,
                type = it.eventType,
                status = if (it.resolved) "resolved" else "open",
                effectText = "A ${it.optionA}：${it.effectA}\nB ${it.optionB}：${it.effectB}\nC ${it.optionC}：${it.effectC}"
            )
        }

    val skills: List<SkillEntity>
        get() = attributes.map {
            SkillEntity(
                name = it.name,
                category = it.category,
                level = it.level,
                exp = it.exp,
                description = it.description
            )
        }

    val relationships = listOf(
        RelationshipEntity(
            name = "真实连接",
            role = "系统概念",
            closeness = 50,
            trust = 50,
            energyEffect = "高质量连接提供恢复、信息和行动反馈。",
            notes = "关系系统的初始占位数据，可由后续关系模块替换。"
        )
    )

    val inventory: List<InventoryEntity>
        get() = assets.map {
            InventoryEntity(
                name = it.name,
                type = it.type,
                quantity = 1,
                valueScore = it.valueScore,
                notes = listOf(it.notes, it.nextAction, it.guideText).filter { text -> text.isNotBlank() }.joinToString("\n")
            )
        }

    val worldSettings: List<WorldSettingEntity>
        get() = settingEntries.map {
            WorldSettingEntity(
                key = it.key,
                title = it.title,
                category = it.category,
                content = it.content
            )
        }

    val journals: List<JournalEntity>
        get() = archives.map {
            JournalEntity(
                title = it.title,
                content = it.content,
                moodScore = it.emotionScore,
                importance = it.importanceScore,
                entryDate = it.date
            )
        }

    val aiMemories = listOf(
        AiMemoryEntity(
            title = "地球OL本地记忆",
            content = "AI 只能读取本地持久化数据，不接云端、不需要登录。未来可通过 Repository 同步层扩展到 Supabase。",
            memoryType = "architecture",
            importance = 5,
            source = "initial_data"
        )
    )

    val player = PlayerEntity(
        name = "邱硕",
        title = "现实玩家 / AI 产品 / 创造玩家",
        level = 12,
        exp = 1240,
        cash = 68000
    )

    fun survivalStatus() = SurvivalStatusEntity(
        energy = 68,
        mental = 74,
        hunger = 45,
        sleep = 60,
        stress = 58,
        focus = 72,
        recovery = 55,
        cashPressure = 45,
        relationshipEnergy = 50,
        date = LocalDate.now().toString(),
        summary = "当前状态适合推进一个明确主线，不适合同时打开太多新项目。"
    )

    val attributes = listOf(
        AttributeEntity(name = "Ti", level = 1, exp = 0, category = "内倾判断", orderIndex = 1, description = "Introverted Thinking，内倾思考：追求逻辑一致、概念精确和系统自洽。"),
        AttributeEntity(name = "Ne", level = 1, exp = 0, category = "外倾感知", orderIndex = 2, description = "Extraverted Intuition，外倾直觉：捕捉可能性、联想、创意分支和新机会。"),
        AttributeEntity(name = "Si", level = 1, exp = 0, category = "内倾感知", orderIndex = 3, description = "Introverted Sensing，内倾感觉：沉淀经验、细节记忆、稳定习惯和可复用流程。"),
        AttributeEntity(name = "Fe", level = 1, exp = 0, category = "外倾判断", orderIndex = 4, description = "Extraverted Feeling，外倾情感：感知群体氛围、照顾关系、协调表达和社会反馈。"),
        AttributeEntity(name = "Te", level = 1, exp = 0, category = "外倾判断", orderIndex = 5, description = "Extraverted Thinking，外倾思考：推动目标、组织资源、制定标准并验证结果。"),
        AttributeEntity(name = "Ni", level = 1, exp = 0, category = "内倾感知", orderIndex = 6, description = "Introverted Intuition，内倾直觉：整合线索、形成洞察、判断趋势和长期方向。"),
        AttributeEntity(name = "Se", level = 1, exp = 0, category = "外倾感知", orderIndex = 7, description = "Extraverted Sensing，外倾感觉：进入当下、捕捉现实信号、行动反应和身体体验。"),
        AttributeEntity(name = "Fi", level = 1, exp = 0, category = "内倾判断", orderIndex = 8, description = "Introverted Feeling，内倾情感：识别真实感受、个人价值、边界和内在认同。")
    )

    val talents = listOf(
        TalentEntity(name = "结构化思考", category = "思维系", level = 4, exp = 40, unlocked = true, orderIndex = 1, description = "把复杂问题拆成可执行模块。"),
        TalentEntity(name = "系统洞察", category = "思维系", level = 3, exp = 60, unlocked = true, orderIndex = 2, description = "从规则、容器和连接中理解现实系统。"),
        TalentEntity(name = "单点推进", category = "行动力", level = 2, exp = 30, unlocked = true, orderIndex = 3, description = "减少多线开战，锁定一个主线。"),
        TalentEntity(name = "快速验证", category = "行动力", level = 3, exp = 20, unlocked = true, orderIndex = 4, description = "用最小动作验证需求和路径。"),
        TalentEntity(name = "AI 编程加速", category = "创造系", level = 4, exp = 70, unlocked = true, orderIndex = 5, description = "使用 AI 快速生成、调试和迭代产品。"),
        TalentEntity(name = "产品原型", category = "创造系", level = 3, exp = 50, unlocked = true, orderIndex = 6, description = "把想法变成可用原型。"),
        TalentEntity(name = "真实连接感知", category = "社交系", level = 1, exp = 20, unlocked = true, orderIndex = 7, description = "识别高质量连接与噪音连接。"),
        TalentEntity(name = "边界感", category = "精神系", level = 1, exp = 30, unlocked = true, orderIndex = 8, description = "不把全部评价权交给外界。"),
        TalentEntity(name = "情绪恢复", category = "精神系", level = 2, exp = 20, unlocked = true, orderIndex = 9, description = "从低谷中重新启动。"),
        TalentEntity(name = "基础作息", category = "生存系", level = 1, exp = 40, unlocked = true, orderIndex = 10, description = "把吃饭、睡觉、运动作为底层主线。")
    )

    val lifeStages = listOf(
        LifeStageEntity(name = "活下去", current = false, orderIndex = 1, description = "稳定吃饭、睡眠、精神状态和现金流，不被系统拖垮。", mainGoal = "维持基础状态。", warning = "长期透支会导致慢性崩溃 Debuff。", nextAction = "保证今天正常吃饭、睡觉和完成一个小任务。", guideText = "这个阶段不是低级阶段，而是所有阶段的底盘。很多玩家不是输给能力，而是输给长期透支。先让角色不崩溃，再谈高级玩法。"),
        LifeStageEntity(name = "成为自己", current = true, orderIndex = 2, description = "不是成为最厉害的人，也不是成为最正确的人，而是在噪音、比较、评价和诱导里，慢慢知道自己是谁。", mainGoal = "确认自己的玩法流派。", warning = "不要被外界标准答案牵着走。", nextAction = "记录一个自己真正愿意长期投入的方向。", guideText = "这个阶段的核心不是证明自己，而是识别自己。你要知道自己适合什么，不适合什么，愿意为什么付代价，不愿意再为什么消耗自己。"),
        LifeStageEntity(name = "建立真实连接", current = false, orderIndex = 3, description = "和别人、工具、城市、平台、世界建立真实连接，解锁更高级的体验和资源。", mainGoal = "形成高质量连接网络。", warning = "无效连接会消耗精神。", nextAction = "维护一个真正重要的关系或连接。", guideText = "连接不是越多越好。有效连接能带来信息、资源、情绪支持或行动反馈。无效连接只会制造噪音和消耗。"),
        LifeStageEntity(name = "长期经营", current = false, orderIndex = 4, description = "短期刺激开始失效，真正重要的是长期稳定、复利、恢复和积累。", mainGoal = "建立长期系统。", warning = "不要被短期反馈牵引。", nextAction = "选择一个可以坚持 100 天的系统。", guideText = "长期经营阶段要减少随机冲动，建立稳定系统。真正改变人生的往往不是一次爆发，而是长期复利。"),
        LifeStageEntity(name = "终局回顾", current = false, orderIndex = 5, description = "回头确认自己体验过、投入过、创造过、爱过、连接过。", mainGoal = "减少遗憾，降低执念。", warning = "不要到最后才发现一直在玩别人的剧本。", nextAction = "写下一个仍然想完成的人生任务。", guideText = "终局不是通关，而是回头确认：我来过，我见过，我做过想做的事，我还有留念，但没有太多挂念。")
    )

    val playerStyles = listOf(
        PlayerStyleEntity(name = "冲榜玩家", selected = false, orderIndex = 1, description = "追求财富、权力、影响力、排名和资源积累。", focus = "现金流、项目、影响力、执行力。", buff = "目标感强，资源整合速度快。", debuff = "压力高，容易过度透支。", guideText = "适合高目标感、高抗压的人。注意别把身体、关系和精神稳定当成无关支线。"),
        PlayerStyleEntity(name = "探索玩家", selected = false, orderIndex = 2, description = "喜欢城市、旅行、观察、体验世界和发现新地图。", focus = "地点、经历、认知、人生宽度。", buff = "视野开阔，容易发现新机会。", debuff = "容易分散，长期积累不足。", guideText = "探索不是乱逛，而是带着问题进入新地图。每次探索都要留下观察、经验或连接。"),
        PlayerStyleEntity(name = "创造玩家", selected = true, orderIndex = 3, description = "通过产品、内容、工具、作品影响世界。", focus = "产品力、表达力、AI 编程、项目资产。", buff = "能把想法变成现实。", debuff = "容易陷入完美主义或多项目过载。", guideText = "创造玩家最重要的是完成闭环：想法 → 原型 → 使用 → 反馈 → 迭代。不要让项目永远停留在设想里。"),
        PlayerStyleEntity(name = "稳定玩家", selected = false, orderIndex = 4, description = "重视低压力生活、稳定现金流、身体恢复和情绪平衡。", focus = "健康、现金流、恢复、生活秩序。", buff = "长期稳定性强。", debuff = "容易错过高风险高收益机会。", guideText = "稳定不是平庸。稳定是高级玩法的底盘。没有稳定，一切高阶玩法都会变成透支。"),
        PlayerStyleEntity(name = "关系玩家", selected = false, orderIndex = 5, description = "重视亲密关系、友情、陪伴、理解和共同成长。", focus = "关系、沟通、情绪稳定、连接。", buff = "恢复力强，人生支持系统更稳。", debuff = "容易被低质量关系消耗。", guideText = "关系是后期资源。真正好的关系是恢复点，不是消耗点。")
    )

    val projects = listOf(
        ProjectEntity(name = "地球 OL App", stage = "安卓 APK 重构", progress = 45, status = "active", orderIndex = 1, nextAction = "完成功能重构并安装到手机实测", description = "把现实人生做成个人 RPG / 经营模拟器，用于管理状态、任务、属性、资产、地点、阶段、天赋、攻略和世界规则。", guideText = "这个项目第一目标不是商业化，而是自用闭环。只要邱硕每天真的愿意打开，它就是成功的。"),
        ProjectEntity(name = "扫码点餐项目", stage = "MVP 原型设计", progress = 35, status = "active", orderIndex = 2, nextAction = "完成后台二维码生成页面，并找 3 家真实商家观察需求", description = "面向线下小商家的扫码点餐工具。", guideText = "不要先幻想大平台。第一步是找真实小店，看老板是否有明确需求，顾客是否愿意扫码点餐。"),
        ProjectEntity(name = "手机控制 Cursor / Codex 工具", stage = "可用链路打通", progress = 65, status = "active", orderIndex = 3, nextAction = "降低消息延迟，并制作一键启动脚本", description = "通过手机输入信息，远程发送到电脑端 Cursor / Codex。", guideText = "这是提高 AI 编程效率的工具。关键不是功能多，而是消息链路稳定、启动简单、延迟足够低。"),
        ProjectEntity(name = "AI 红娘 / 婚恋社交项目", stage = "产品方向探索", progress = 25, status = "active", orderIndex = 4, nextAction = "明确国内版和海外版的差异，设计最小可验证匹配流程", description = "基于 AI + 婚恋 + 社交的产品方向。", guideText = "婚恋社交难点在信任、安全、供需平衡和冷启动。第一版不要做大社交，只做最小匹配闭环。"),
        ProjectEntity(name = "身体恢复项目", stage = "基础恢复", progress = 30, status = "active", orderIndex = 5, nextAction = "连续 7 天保持每天 2-3 顿正常饭和适量运动", description = "通过正常吃饭、骑车、散步、睡眠和减少过度消耗恢复身体和精神状态。", guideText = "身体恢复不是支线，而是所有项目的底层 Buff。身体崩了，所有主线都会掉线。"),
        ProjectEntity(name = "现金流项目筛选", stage = "机会筛选", progress = 25, status = "active", orderIndex = 6, nextAction = "用合法性、短路径、可验证性筛选项目", description = "从众多想法中筛选合法、短路径、低成本、能快速验证的现金流项目。", guideText = "现金流项目最怕幻想。标准只有三个：是否合法，是否短路径，是否能 7 天内验证。")
    )

    fun tasks(projectIds: Map<String, Long>) = listOf(
        task("完成地球 OL 功能重构", "main", 120, "AI 编程", 25, "地球 OL App", projectIds, "先跑通核心闭环，不要纠结完美 UI。功能重构完成后，立刻安装到手机实测。"),
        task("打包并安装地球 OL APK 到安卓手机", "main", 120, "执行力", 25, "地球 OL App", projectIds, "从代码到实机运行是关键闭环。能在手机上用，才算项目进入现实世界。"),
        task("完成扫码点餐后台二维码生成页", "main", 90, "产品力", 20, "扫码点餐项目", projectIds, "二维码生成是商家侧关键功能。先做能用版本，不要一开始做复杂后台。"),
        task("找 3 家没有扫码点餐的小店观察流程", "main", 80, "商业嗅觉", 15, "扫码点餐项目", projectIds, "不要只在脑子里验证。真实小店的老板、顾客、收款方式和点餐流程，才是有效信息。"),
        task("制作手机控制 Cursor 的一键启动脚本", "main", 80, "AI 编程", 15, "手机控制 Cursor / Codex 工具", projectIds, "这个任务的目标是降低启动成本。以后每次开电脑，都能一键恢复手机控制链路。"),
        task("正常吃两顿饭", "survival", 30, "身体值", 10, "身体恢复项目", projectIds, "这是生存任务，不是普通打卡。长期吃不好，会影响精神、判断力和执行力。"),
        task("骑车或散步 30 分钟", "daily", 30, "身体值", 10, "身体恢复项目", projectIds, "低成本运动是恢复精神值的有效方式。不要追求强度，先追求稳定。"),
        task("今天只推进一个主线项目", "daily", 40, "执行力", 10, "现金流项目筛选", projectIds, "防止多项目过载。今天只抓一个核心矛盾，其他想法先进入资产库。"),
        task("记录一个真实商业观察", "daily", 40, "商业嗅觉", 10, "现金流项目筛选", projectIds, "每天从现实中找一个需求，而不是只在聊天和想象里设计产品。"),
        task("写 300 字复盘", "daily", 35, "认知力", 10, "地球 OL App", projectIds, "复盘不是写日记，而是把今天的行动、判断、错误和发现沉淀成经验。"),
        TaskEntity(title = "Boss：完成第一个可长期自用的地球 OL APK", description = "做出每天真的愿意打开的个人操作系统。", type = "boss", expReward = 200, attributeName = "AI 编程", attributeReward = 40, projectId = projectIds["地球 OL App"], bossMaxHp = 1000, bossCurrentHp = 1000, guideText = "目标不是做花哨 App，而是做出邱硕每天真的会打开、能驱动现实行动的个人操作系统。"),
        TaskEntity(title = "Boss：跑通第一个真实现金流 MVP", description = "选出一个合法、短路径、能真实验证支付意愿的项目。", type = "boss", expReward = 300, attributeName = "现金流", attributeReward = 50, projectId = projectIds["现金流项目筛选"], bossMaxHp = 1500, bossCurrentHp = 1500, guideText = "目标是在多个想法里选出一个合法、短路径、能真实验证支付意愿的项目。")
    )

    private fun task(title: String, type: String, exp: Int, attr: String, attrExp: Int, project: String, projectIds: Map<String, Long>, guide: String) =
        TaskEntity(title = title, description = guide, type = type, expReward = exp, attributeName = attr, attributeReward = attrExp, projectId = projectIds[project], guideText = guide)

    val assets = listOf(
        AssetEntity(name = "现金储备", type = "cash", valueScore = 68, potentialScore = 50, maintenanceCost = "低", nextAction = "保持现金流安全垫", notes = "当前现金 68000", guideText = "现金是短期生存资源，但不是唯一资产。", orderIndex = 1),
        AssetEntity(name = "MacBook Air M4", type = "device", valueScore = 80, potentialScore = 60, maintenanceCost = "低", nextAction = "用于 AI 编程和产品开发", notes = "", guideText = "设备资产要服务产出，而不是只停留在拥有。", orderIndex = 2),
        AssetEntity(name = "AI 编程能力", type = "skill", valueScore = 75, potentialScore = 95, maintenanceCost = "中", nextAction = "持续用 Codex 做项目", notes = "", guideText = "能力资产的价值来自持续产出。", orderIndex = 3),
        AssetEntity(name = "地球 OL App", type = "project", valueScore = 45, potentialScore = 90, maintenanceCost = "中", nextAction = "完成重构并实机使用", notes = "", guideText = "自用闭环是第一价值。", orderIndex = 4)
    )

    val places = listOf(
        PlaceEntity(name = "家中工作台", type = "work", efficiencyScore = 75, recoveryScore = 55, opportunityScore = 40, costLevel = "low", buff = "专注开发", debuff = "久坐和闭环不足", notes = "适合编码和产品设计", guideText = "工作台是创造玩家的主城。", orderIndex = 1),
        PlaceEntity(name = "骑行路线", type = "sport", efficiencyScore = 35, recoveryScore = 85, opportunityScore = 20, costLevel = "low", buff = "恢复体力", debuff = "机会密度低", notes = "恢复体力和情绪", guideText = "低成本运动是恢复蓝量的方式。", orderIndex = 2),
        PlaceEntity(name = "线下小店街区", type = "business", efficiencyScore = 60, recoveryScore = 35, opportunityScore = 80, costLevel = "medium", buff = "真实商业观察", debuff = "信息噪音", notes = "观察扫码点餐真实需求", guideText = "商业机会来自真实场景。", orderIndex = 3)
    )

    val hiddenQuests = listOf(
        HiddenQuestEntity(title = "接受自己不是世界中心", status = "discovered", orderIndex = 1, description = "不再默认所有人都应该理解你，也不再用外界反应定义自己。", triggerCondition = "经历评价波动后显形", rewardText = "精神稳定 +1，自我认同 +1", guideText = "这个任务不是让你否定自己，而是让你从外界评价里收回主控权。"),
        HiddenQuestEntity(title = "找到愿意长期投入的事", status = "discovered", orderIndex = 2, description = "不是短期兴奋，而是找到一个愿意长期打磨的方向。", triggerCondition = "多项目筛选后显形", rewardText = "长期主义 +1，创造系经验提升", guideText = "判断标准不是今天兴奋，而是低谷时是否仍愿意继续。"),
        HiddenQuestEntity(title = "学会和局限和平共处", status = "hidden", orderIndex = 3, description = "承认有些出生点、环境、身体和历史无法改变，但仍然可以选择怎么活。", triggerCondition = "长期自我和解后显形", rewardText = "恢复能力 +1，执念值下降", guideText = "和平共处不是认输，而是不再把全部能量浪费在不可逆问题上。"),
        HiddenQuestEntity(title = "不再用焦虑假装努力", status = "discovered", orderIndex = 4, description = "停止同时打开太多支线，回到今天真正能推进的一件事。", triggerCondition = "多线过载时显形", rewardText = "执行力 +1，精神压力下降", guideText = "焦虑会制造忙碌感，但不会自动产生进度。真正的努力通常很具体。"),
        HiddenQuestEntity(title = "识别自己的错误容器", status = "hidden", orderIndex = 5, description = "意识到限制自己的不是能力，而是环境、圈层、睡眠、关系或现金流结构。", triggerCondition = "换地图前显形", rewardText = "系统洞察 +1，换地图能力提升", guideText = "当你在错误容器里努力，努力会变成消耗。识别容器，是换地图的前提。")
    )

    val randomEvents = listOf(
        RandomEventEntity(title = "突然出现一个新项目想法", eventType = "灵感", description = "你脑子里突然出现一个看起来很有潜力的新项目。", optionA = "立刻开干", effectA = "短期兴奋 +20，但任务线负担 +30", optionB = "记录到资产库，暂不启动", effectB = "认知力 +10，执行力稳定", optionC = "用 7 天验证标准筛选", effectC = "商业嗅觉 +15，现金流判断 +10", resolved = false, guideText = "新想法不是不能做，而是要先进入筛选机制。不要让每个灵感都变成新项目。"),
        RandomEventEntity(title = "今天精神状态下降", eventType = "低谷", description = "你发现自己今天不太想做事，情绪和体力都偏低。", optionA = "强行推进高难任务", effectA = "可能完成任务，但压力 +25", optionB = "只完成一个小任务", effectB = "执行力 +5，恢复 +10", optionC = "进入恢复模式", effectC = "身体值 +10，精神稳定 +10", resolved = false, guideText = "低谷日不是废掉的一天。低谷日的胜利条件是不要让系统继续崩。"),
        RandomEventEntity(title = "遇到一个可能有价值的人", eventType = "连接", description = "你遇到一个可能带来信息、机会或合作的人。", optionA = "主动交流", effectA = "连接值 +15", optionB = "礼貌观察", effectB = "认知力 +5", optionC = "忽略", effectC = "无变化", resolved = false, guideText = "连接不是讨好。有效连接的核心是信息、资源、反馈或共同成长。"),
        RandomEventEntity(title = "发现当前环境效率很低", eventType = "地图", description = "你意识到当前地图不适合长期成长。", optionA = "继续忍", effectA = "稳定性 +5，但成长速度下降", optionB = "寻找新地图", effectB = "探索值 +15，压力 +10", optionC = "先短期过渡", effectC = "恢复 +10，机会密度不变", resolved = false, guideText = "换地图不是逃避。当前容器限制成长时，换地图就是战略动作。")
    )

    val archives = listOf(
        LifeArchiveEntity(title = "地球 OL 重构启动", type = "project", date = LocalDate.now().toString(), emotionScore = 70, importanceScore = 95, content = "把普通待办重构为现实人生游戏化操作系统。"),
        LifeArchiveEntity(title = "当前阶段确认：成为自己", type = "reflection", date = LocalDate.now().toString(), emotionScore = 65, importanceScore = 90, content = "当前主线不是成为标准答案，而是确认自己的玩法流派。")
    )

    val worldRules = listOf(
        rule("没有唯一主线", "世界观", "地球 OL 没有官方唯一主线。大多数玩家会走上读书、工作、赚钱、结婚、买房、养育后代、养老送终这些默认路线。但这不代表这些路线就是唯一正确答案。真正的开放世界没有固定主线。你的每一个选择都会影响未来。有人主线是财富，有人主线是创造，有人主线是探索，有人主线是关系，有人主线是平静，有人主线是影响世界。系统不会告诉你哪条路一定正确。你需要在体验、失败、恢复和选择中，慢慢确认自己的玩法。", 1),
        rule("生存任务是底层主线", "生存规则", "很多玩家年轻时以为，人生主线应该是宏大叙事。但玩久了会发现：稳定吃饭，稳定睡觉，稳定情绪，维持现金流，保持基础体力和精神值，本身就是高难副本。这些任务听起来没意思，但它们是所有高级玩法的前提。长期睡眠不足、长期高压、长期现金流赤字、长期情绪透支，会给角色叠加慢性崩溃 Debuff。很多玩家不是输给能力，而是输给长期透支。", 2),
        rule("成长任务没有说明书", "成长规则", "地球 OL 不会明确告诉你如何成长。你会被迫学习怎么和人说话，怎么处理关系，怎么面对失败，怎么判断自己想要什么，怎么接受很多努力不一定有结果。很多成长任务没有进度条。你以为自己已经完成了，几年后旧剧情触发，才发现那个任务只是被搁置了。真正的成长，经常不是获得新技能，而是修复旧漏洞。", 3),
        rule("身份任务会同时开启", "隐藏机制", "地球 OL 里有大量身份系统。学生、打工人、创业者、伴侣、朋友、子女、父母、管理者、照顾者、创作者、探索者。这些身份像职业分支，每条线都有专属任务包，也有对应 Debuff。重点是这些支线不是分开的，而是同时开启的。", 4),
        rule("随机事件是最离谱的机制", "隐藏机制", "地球 OL 没有绝对公平，但特别喜欢随机刷新事件。突然遇到一个重要的人，突然生一场病，突然有一个机会砸到你头上，突然一切都不顺。随机事件不看你当前等级，也不问你准备好了没有。所以真正稀缺的不是天赋，而是韧性、恢复能力和重新开局的心态。", 5),
        rule("隐藏任务不是系统派发的", "隐藏机制", "隐藏任务不会直接弹出来，只会在你经历一些事后慢慢显形。接受自己不是世界中心，不再执着所有人都理解你，找到一种愿意长期投入的事，学会和自己的局限和平共处。这些任务的奖励通常不是金币，而是你慢慢变得稳一点、清醒一点。", 6),
        rule("容器决定成长上限", "容器规则", "人在绝大多数环境中，都会受到容器上限限制。时间、城市、圈层、身体都是容器。所谓成长，不只是努力，也包括识别自己当前容器的瓶颈。当一个环境无法继续提供成长空间时，换地图本身就是升级动作。", 7),
        rule("连接产生级联放大", "连接规则", "地球 OL 不是单机游戏。连接，是这个世界最重要的底层机制之一。人和人连接产生关系，人和工具连接产生效率，人和平台连接产生传播，人和 AI 连接放大创造力。但连接不是越多越好。真正有效的连接，是能稳定产生信息、资源、情绪支持或行动反馈的连接。", 8),
        rule("天赋树需要自己挖出来", "成长规则", "地球 OL 屏蔽了显性的等级、经验和技能树。你需要自己把隐藏着的知识、技能、能力路径挖出来。真正高级的玩家，会结合自己的能力、环境、资源和兴趣，设计一条适合自己的成长路线。", 9),
        rule("终局不是通关", "终局哲学", "地球 OL 没有传统意义上的通关。终局更像是：我体验过，我见过，我投入过，我爱过，我创造过，我和世界真实连接过。最后能够回头说：这个世界我来过。我还有留念，但没有太多挂念。", 10)
    )

    private fun rule(title: String, category: String, content: String, index: Int) =
        WorldRuleEntity(title = title, category = category, content = content, summary = content.take(70), orderIndex = index)

    val guides = listOf(
        guide("Earth OL 总攻略：如何玩好这款真实人生游戏", "这不是成功学，而是老玩家写给新玩家的生存说明。", "总攻略", "欢迎来到 Earth OL。\n\n这不是一款公平游戏，也不是一款有明确主线的游戏。\n\n你不会在出生时获得完整说明书。你不会自动知道自己适合什么。你不会天然拥有稳定资源。你也不会每次努力都有即时反馈。\n\n但这不代表游戏不能玩。\n\nEarth OL 的第一原则是：先活下去。\n\n稳定吃饭、稳定睡觉、稳定情绪、维持现金流，是所有高级玩法的前提。\n\n第二原则是：不要同时开太多支线。\n\n第三原则是：选择比努力更重要。\n\n第四原则是：恢复能力是隐藏属性。\n\n第五原则是：你不需要成为标准答案。\n\n最终，你要形成自己的玩法：知道自己是谁，知道自己适合什么，知道当前阶段该做什么，知道哪些任务该放弃，知道哪些连接值得维护，知道如何从低谷里重新启动。", "overview", 1),
        guide("首页攻略：如何使用人生驾驶舱", "", "首页攻略", "首页不是展示数据的地方，而是你的每日驾驶舱。\n\n每天打开地球 OL，先看今日策略、生存状态和当前 Debuff。\n\n首页的正确使用方式不是让你更焦虑，而是告诉你：今天该推进，还是该恢复；该猛攻，还是该止损。", "home", 2),
        guide("任务攻略：不要把所有事情都塞进任务列表", "", "任务攻略", "任务系统的核心不是记录所有待办，而是筛选真正值得推进的行动。\n\n每天最多只设 1 个主线任务。日常任务不要超过 3 个。Boss 任务不要期待一天打完。生存任务优先级高于多数项目任务。", "tasks", 3),
        guide("角色攻略：你不是标签，你是可成长系统", "", "角色攻略", "角色页展示的是你的长期成长结构。\n\n等级不是为了虚荣，战斗力也不是为了和别人比较。它们只是帮你看到哪些能力已经形成，哪些属性正在拖后腿，哪些天赋已经解锁。", "character", 4),
        guide("生存攻略：稳定状态是最底层主线", "", "生存攻略", "不要小看吃饭、睡觉、运动和恢复。\n\n睡眠不足会降低判断力。精神赤字会降低执行力。现金流压力会提高冒险冲动。不要用透支的角色去打高级副本。", "survival", 5),
        guide("天赋攻略：现实天赋树需要自己挖", "", "天赋攻略", "现实世界不会显示技能树，但技能树确实存在。\n\n邱硕当前适合优先加点：AI 编程、产品设计、单点推进、恢复能力、现金流判断。", "talents", 6),
        guide("项目攻略：项目不是想法，是持续推进的资产", "", "项目攻略", "项目不是脑子里的点子。项目必须有当前阶段、下一步行动、进度、真实反馈和可验证结果。\n\n每天只选一个主线项目，每周只验证一个商业方向。", "projects", 7),
        guide("资产攻略：把能力、设备、经验和项目都看成资产", "", "资产攻略", "资产不只是钱。你的设备、能力、项目、经历、内容和连接都是资产。\n\n不要只盯着现金，要把自己能持续产生价值的东西都盘出来。", "assets", 8),
        guide("地图攻略：换地图也是升级", "", "地图攻略", "地图不是地点列表，而是环境容器。\n\n不同地图会提供不同 Buff 和 Debuff。当一个环境无法继续提供成长空间时，换地图本身就是升级动作。", "map", 9),
        guide("世界攻略：先理解规则，再谈改变命运", "", "世界攻略", "理解世界规则不是为了变得冷漠，而是为了减少无效挣扎。\n\n有些问题不是努力问题，而是容器问题。有些机会不是能力问题，而是连接问题。", "world", 10),
        guide("隐藏任务攻略：真正改变你的任务通常不会弹窗", "", "隐藏任务攻略", "隐藏任务通常在你经历失败、关系、低谷、选择、失去和重启后显形。奖励通常不是金币，而是更稳、更清醒。", "hidden", 11),
        guide("随机事件攻略：系统不会等你准备好", "", "随机事件攻略", "地球 OL 的随机事件非常频繁。应对随机事件的关键不是预测一切，而是保留余量：现金、精神、时间、身体和重新开局能力。", "events", 12),
        guide("人生阶段攻略：不同阶段不要用同一套打法", "", "人生阶段攻略", "活下去阶段目标是稳定，成为自己阶段目标是识别，建立连接阶段目标是高质量关系和资源网络。先判断自己在哪个阶段，再决定该做什么任务。", "stages", 13),
        guide("玩家流派攻略：不要玩不适合自己的 Build", "", "玩家流派攻略", "没有哪个流派天然高级。邱硕当前默认流派是创造玩家：不要只想，要做出作品，形成从想法到原型到反馈到迭代的闭环。", "style", 14),
        guide("档案攻略：记录不是怀旧，是保存人生证据", "", "档案攻略", "档案页不是日记本，而是人生存档。不要只记录结果，也记录过程；不要只记录成功，也记录失败。", "archive", 15)
    )

    private fun guide(title: String, subtitle: String, category: String, content: String, module: String, index: Int) =
        GuideEntity(title = title, subtitle = subtitle, category = category, content = content, relatedModule = module, orderIndex = index)
}
