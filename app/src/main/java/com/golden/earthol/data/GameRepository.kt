package com.golden.earthol.data

import androidx.room.withTransaction
import com.golden.earthol.data.entity.AiMemoryEntity
import com.golden.earthol.data.entity.JournalEntity
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.data.entity.RelationshipEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.logic.AiStatChanges
import com.golden.earthol.logic.GameLogic
import com.golden.earthol.logic.WorldSaveJson
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class GameRepository(private val db: AppDatabase) {
    val player = db.playerDao().observePlayer()
    val survivalStatus = db.survivalStatusDao().observeLatest()
    val attributes = db.attributeDao().observeAttributes()
    val talents = db.talentDao().observeTalents()
    val tasks = db.taskDao().observeTasks()
    val projects = db.projectDao().observeProjects()
    val assets = db.assetDao().observeAssets()
    val places = db.placeDao().observePlaces()
    val lifeStages = db.lifeStageDao().observeStages()
    val playerStyles = db.playerStyleDao().observeStyles()
    val worldRules = db.worldRuleDao().observeRules()
    val guides = db.guideDao().observeGuides()
    val hiddenQuests = db.hiddenQuestDao().observeHiddenQuests()
    val randomEvents = db.randomEventDao().observeEvents()
    val lifeArchives = db.lifeArchiveDao().observeArchives()
    val libraryContents = db.libraryContentDao().observeContents()
    val debuffs = db.debuffDao().observeDebuffs()
    val settingEntries = db.settingEntryDao().observeEntries()
    val worldView = db.settingEntryDao().observeByKey("world_view")
    val events = db.eventDao().observeAll()
    val skills = db.skillDao().observeAll()
    val relationships = db.relationshipDao().observeAll()
    val inventory = db.inventoryDao().observeAll()
    val worldSettings = db.worldSettingDao().observeAll()
    val journals = db.journalDao().observeAll()
    val aiMemories = db.aiMemoryDao().observeAll()

    suspend fun updateSurvivalStatus(status: SurvivalStatusEntity) {
        if (status.id == 0L) {
            db.survivalStatusDao().insert(status)
        } else {
            db.survivalStatusDao().update(status)
        }
    }

    suspend fun libraryContentsSnapshot(): List<LibraryContentEntity> = db.libraryContentDao().getAll()

    suspend fun importLibraryContent(content: LibraryContentEntity) = db.libraryContentDao().insert(content)

    suspend fun updateAttributeValue(attributeId: Long, value: Int) {
        val current = db.attributeDao().getAll().firstOrNull { it.id == attributeId } ?: return
        db.attributeDao().update(current.copy(exp = value.coerceIn(0, 100)))
    }

    suspend fun updatePlayerProgress(level: Int, exp: Int) {
        db.playerDao().getPlayer()?.let { player ->
            db.playerDao().update(
                player.copy(
                    level = level.coerceAtLeast(1),
                    exp = exp.coerceAtLeast(0),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun addJournalEntry(title: String, content: String, entryDate: String) {
        db.journalDao().upsert(
            JournalEntity(
                title = title,
                content = content,
                moodScore = 0,
                importance = 3,
                entryDate = entryDate
            )
        )
    }

    suspend fun applyAiStatChanges(changes: AiStatChanges, userMessage: String, aiReply: String) = db.withTransaction {
        val latestStatus = db.survivalStatusDao().getLatest() ?: InitialData.survivalStatus()
        val nextStatus = latestStatus.copy(
            energy = (latestStatus.energy + changes.stamina).statusRange(),
            mental = (latestStatus.mental + changes.san + changes.spirit + changes.dopamine + changes.meaning - changes.loneliness).statusRange(),
            hunger = (latestStatus.hunger + changes.hunger).statusRange(),
            sleep = (latestStatus.sleep + changes.sleep).statusRange(),
            stress = (latestStatus.stress + changes.pressure).statusRange(),
            focus = (latestStatus.focus + changes.focus + changes.actionPower).statusRange(),
            recovery = (latestStatus.recovery + changes.health).statusRange(),
            relationshipEnergy = (latestStatus.relationshipEnergy + changes.relationshipEnergy).statusRange(),
            summary = aiReply
        )
        if (nextStatus.id == 0L) {
            db.survivalStatusDao().insert(nextStatus)
        } else {
            db.survivalStatusDao().update(nextStatus)
        }

        db.playerDao().getPlayer()?.let { player ->
            if (changes.money != 0) {
                db.playerDao().update(
                    player.copy(
                        cash = (player.cash + changes.money).coerceAtLeast(0),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        val today = LocalDate.now().toString()
        db.journalDao().upsert(
            JournalEntity(
                title = "系统日志 $today",
                content = "玩家输入：$userMessage\n\n观察者回应：$aiReply",
                moodScore = 0,
                importance = 3,
                entryDate = today
            )
        )
    }

    suspend fun recentAiMemories(limit: Int): List<AiMemoryEntity> =
        db.aiMemoryDao().getAll().take(limit)

    suspend fun upsertAiMemory(memory: AiMemoryEntity): Long =
        db.aiMemoryDao().upsert(memory.copy(updatedAt = System.currentTimeMillis()))

    suspend fun upsertRelationship(relationship: RelationshipEntity): Long =
        db.relationshipDao().upsert(relationship.copy(updatedAt = System.currentTimeMillis()))

    suspend fun deleteRelationship(relationship: RelationshipEntity) =
        db.relationshipDao().delete(relationship)

    suspend fun addTalent(name: String, category: String, level: Int, description: String, unlocked: Boolean) {
        val nextOrder = (db.talentDao().getAll().maxOfOrNull { it.orderIndex } ?: 0) + 1
        db.talentDao().insert(
            TalentEntity(
                name = name.trim(),
                category = category.trim(),
                level = level.coerceIn(1, 100),
                exp = 0,
                description = description.trim(),
                unlocked = unlocked,
                orderIndex = nextOrder
            )
        )
    }

    suspend fun deleteTalent(talent: TalentEntity) =
        db.talentDao().delete(talent)

    suspend fun exportWorldSaveJson(): String = db.withTransaction {
        WorldSaveJson.encode(
            player = db.playerDao().getPlayer(),
            tasks = db.taskDao().getAll(),
            events = db.eventDao().getAll(),
            skills = db.skillDao().getAll(),
            relationships = db.relationshipDao().getAll(),
            inventory = db.inventoryDao().getAll(),
            worldSettings = db.worldSettingDao().getAll(),
            journals = db.journalDao().getAll(),
            guides = db.guideDao().getAll(),
            aiMemories = db.aiMemoryDao().getAll()
        )
    }

    suspend fun exportFullDatabaseJson(): String = db.withTransaction {
        val data = JSONObject()
            .put("players", listOfNotNull(db.playerDao().getPlayer()).toJsonArray())
            .put("survival_status", db.survivalStatusDao().getAll().toJsonArray())
            .put("attributes", db.attributeDao().getAll().toJsonArray())
            .put("talents", db.talentDao().getAll().toJsonArray())
            .put("tasks", db.taskDao().getAll().toJsonArray())
            .put("projects", db.projectDao().getAll().toJsonArray())
            .put("assets", db.assetDao().getAll().toJsonArray())
            .put("places", db.placeDao().getAll().toJsonArray())
            .put("life_stages", db.lifeStageDao().getAll().toJsonArray())
            .put("player_styles", db.playerStyleDao().getAll().toJsonArray())
            .put("world_rules", db.worldRuleDao().getAll().toJsonArray())
            .put("guides", db.guideDao().getAll().toJsonArray())
            .put("hidden_quests", db.hiddenQuestDao().getAll().toJsonArray())
            .put("random_events", db.randomEventDao().getAll().toJsonArray())
            .put("life_archives", db.lifeArchiveDao().getAll().toJsonArray())
            .put("library_contents", db.libraryContentDao().getAll().toJsonArray())
            .put("debuffs", db.debuffDao().getAll().toJsonArray())
            .put("setting_entries", db.settingEntryDao().getAll().toJsonArray())
            .put("events", db.eventDao().getAll().toJsonArray())
            .put("skills", db.skillDao().getAll().toJsonArray())
            .put("relationships", db.relationshipDao().getAll().toJsonArray())
            .put("inventory", db.inventoryDao().getAll().toJsonArray())
            .put("world_settings", db.worldSettingDao().getAll().toJsonArray())
            .put("journals", db.journalDao().getAll().toJsonArray())
            .put("ai_memories", db.aiMemoryDao().getAll().toJsonArray())

        JSONObject()
            .put("type", "earth_ol_full_room_database")
            .put("schemaVersion", 1)
            .put("databaseName", "earth_ol.db")
            .put("exportedAt", System.currentTimeMillis())
            .put("tableCount", data.length())
            .put("data", data)
            .toString(2)
    }

    suspend fun importWorldSaveJson(json: String) = db.withTransaction {
        val save = WorldSaveJson.decode(json)
        db.aiMemoryDao().clear()
        db.guideDao().clear()
        db.journalDao().clear()
        db.worldSettingDao().clear()
        db.inventoryDao().clear()
        db.relationshipDao().clear()
        db.skillDao().clear()
        db.eventDao().clear()
        db.taskDao().clear()
        db.playerDao().clear()

        save.player?.let { db.playerDao().insert(it) }
        db.taskDao().insertAll(save.tasks)
        db.eventDao().insertAll(save.events)
        db.skillDao().insertAll(save.skills)
        db.relationshipDao().insertAll(save.relationships)
        db.inventoryDao().insertAll(save.inventory)
        db.worldSettingDao().insertAll(save.worldSettings)
        db.journalDao().insertAll(save.journals)
        db.guideDao().insertAll(save.guides)
        db.aiMemoryDao().insertAll(save.aiMemories)
    }

    suspend fun currentSettingDocumentSnapshot(): SettingDocumentSnapshot = db.withTransaction {
        SettingDocumentSnapshot(
            player = db.playerDao().getPlayer(),
            survivalStatus = db.survivalStatusDao().getLatest(),
            settingEntries = db.settingEntryDao().getAll(),
            worldRules = db.worldRuleDao().getAll(),
            guides = db.guideDao().getAll(),
            attributes = db.attributeDao().getAll(),
            talents = db.talentDao().getAll(),
            tasks = db.taskDao().getAll(),
            projects = db.projectDao().getAll(),
            assets = db.assetDao().getAll(),
            places = db.placeDao().getAll(),
            lifeStages = db.lifeStageDao().getAll(),
            playerStyles = db.playerStyleDao().getAll(),
            hiddenQuests = db.hiddenQuestDao().getAll(),
            randomEvents = db.randomEventDao().getAll(),
            lifeArchives = db.lifeArchiveDao().getAll(),
            libraryContents = db.libraryContentDao().getAll(),
            debuffs = db.debuffDao().getAll()
        )
    }

    suspend fun initializeDefaultData() = db.withTransaction {
        if (db.playerDao().getPlayer() != null) {
            if (db.settingEntryDao().getAll().isEmpty()) {
                db.settingEntryDao().insertAll(InitialData.settingEntries)
            }
            refreshMbtiAttributes()
            seedCanonicalTablesIfEmpty()
            return@withTransaction
        }

        val stageIds = db.lifeStageDao().insertAll(InitialData.lifeStages)
        val styleIds = db.playerStyleDao().insertAll(InitialData.playerStyles)
        val currentStageIndex = InitialData.lifeStages.indexOfFirst { it.current }.coerceAtLeast(0)
        val currentStyleIndex = InitialData.playerStyles.indexOfFirst { it.selected }.coerceAtLeast(0)
        db.playerDao().insert(
            InitialData.player.copy(
                currentStageId = stageIds.getOrNull(currentStageIndex),
                currentStyleId = styleIds.getOrNull(currentStyleIndex)
            )
        )

        db.survivalStatusDao().insert(InitialData.survivalStatus())
        db.attributeDao().insertAll(InitialData.attributes)
        db.talentDao().insertAll(InitialData.talents)

        val projectIds = db.projectDao().insertAll(InitialData.projects)
        val projectIdMap = InitialData.projects.mapIndexed { index, project ->
            project.name to projectIds[index]
        }.toMap()
        db.taskDao().insertAll(InitialData.tasks(projectIdMap))

        db.assetDao().insertAll(InitialData.assets)
        db.placeDao().insertAll(InitialData.places)
        db.worldRuleDao().insertAll(InitialData.worldRules)
        db.guideDao().insertAll(InitialData.guides)
        db.settingEntryDao().insertAll(InitialData.settingEntries)
        db.hiddenQuestDao().insertAll(InitialData.hiddenQuests)
        db.randomEventDao().insertAll(InitialData.randomEvents)
        db.lifeArchiveDao().insertAll(InitialData.archives)
        db.debuffDao().insertAll(GameLogic.judgeDebuffs(InitialData.survivalStatus()))
        seedCanonicalTablesIfEmpty()
    }

    private suspend fun refreshMbtiAttributes() {
        val current = db.attributeDao().getAll()
        val mbtiNames = InitialData.attributes.map { it.name }
        if (current.map { it.name } == mbtiNames) return

        db.attributeDao().clear()
        db.attributeDao().insertAll(InitialData.attributes)
    }

    private suspend fun seedCanonicalTablesIfEmpty() {
        if (db.eventDao().getAll().isEmpty()) db.eventDao().insertAll(InitialData.events)
        if (db.skillDao().getAll().isEmpty()) db.skillDao().insertAll(InitialData.skills)
        if (db.relationshipDao().getAll().isEmpty()) db.relationshipDao().insertAll(InitialData.relationships)
        if (db.inventoryDao().getAll().isEmpty()) db.inventoryDao().insertAll(InitialData.inventory)
        if (db.worldSettingDao().getAll().isEmpty()) db.worldSettingDao().insertAll(InitialData.worldSettings)
        if (db.journalDao().getAll().isEmpty()) db.journalDao().insertAll(InitialData.journals)
        if (db.aiMemoryDao().getAll().isEmpty()) db.aiMemoryDao().insertAll(InitialData.aiMemories)
    }

    suspend fun completeTask(task: TaskEntity) = db.withTransaction {
        val current = db.taskDao().getById(task.id) ?: return@withTransaction
        if (current.status == "done") return@withTransaction

        if (current.type == "boss") {
            db.taskDao().update(current.copy(status = "done", completedAt = System.currentTimeMillis(), bossCurrentHp = 0))
        } else {
            db.taskDao().update(current.copy(status = "done", completedAt = System.currentTimeMillis()))
        }

        db.playerDao().getPlayer()?.let { player ->
            db.playerDao().update(GameLogic.applyPlayerExp(player, current.expReward))
        }

        current.attributeName?.let { name ->
            db.attributeDao().getByName(name)?.let { attribute ->
                db.attributeDao().update(GameLogic.applyAttributeExp(attribute, current.attributeReward))
            }
        }

        current.projectId?.let { projectId ->
            db.projectDao().getById(projectId)?.let { project ->
                db.projectDao().update(project.copy(progress = (project.progress + 5).coerceAtMost(100)))
            }
            if (current.type != "boss") damageProjectBoss(projectId, current.expReward)
        }
    }

    private suspend fun damageProjectBoss(projectId: Long, damage: Int) {
        val boss = db.taskDao().observeTasksSnapshot().firstOrNull {
            it.type == "boss" && it.projectId == projectId && it.status != "done"
        } ?: return
        val nextHp = ((boss.bossCurrentHp ?: boss.bossMaxHp ?: 0) - damage).coerceAtLeast(0)
        db.taskDao().update(
            boss.copy(
                bossCurrentHp = nextHp,
                status = if (nextHp <= 0) "done" else boss.status,
                completedAt = if (nextHp <= 0) System.currentTimeMillis() else boss.completedAt
            )
        )
    }

    private fun List<*>.toJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { item -> item?.let { array.put(it.toJsonObject()) } }
        return array
    }

    private fun Any.toJsonObject(): JSONObject {
        val json = JSONObject()
        javaClass.declaredFields
            .filter { !it.isSynthetic }
            .forEach { field ->
                field.isAccessible = true
                json.put(field.name, field.get(this))
            }
        return json
    }

    private fun Int.statusRange(): Int = coerceIn(0, 100)
}
