package com.golden.earthol.logic

import com.golden.earthol.data.entity.AiMemoryEntity
import com.golden.earthol.data.entity.EventEntity
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.data.entity.InventoryEntity
import com.golden.earthol.data.entity.JournalEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.RelationshipEntity
import com.golden.earthol.data.entity.SkillEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.WorldSettingEntity
import org.json.JSONArray
import org.json.JSONObject

data class WorldSaveData(
    val player: PlayerEntity?,
    val tasks: List<TaskEntity>,
    val events: List<EventEntity>,
    val skills: List<SkillEntity>,
    val relationships: List<RelationshipEntity>,
    val inventory: List<InventoryEntity>,
    val worldSettings: List<WorldSettingEntity>,
    val journals: List<JournalEntity>,
    val guides: List<GuideEntity>,
    val aiMemories: List<AiMemoryEntity>
)

object WorldSaveJson {
    fun encode(
        player: PlayerEntity?,
        tasks: List<TaskEntity>,
        events: List<EventEntity>,
        skills: List<SkillEntity>,
        relationships: List<RelationshipEntity>,
        inventory: List<InventoryEntity>,
        worldSettings: List<WorldSettingEntity>,
        journals: List<JournalEntity>,
        guides: List<GuideEntity>,
        aiMemories: List<AiMemoryEntity>
    ): String {
        val data = JSONObject()
            .put("player", player?.toJsonObject())
            .put("tasks", tasks.toJsonArray())
            .put("events", events.toJsonArray())
            .put("skills", skills.toJsonArray())
            .put("relationships", relationships.toJsonArray())
            .put("inventory", inventory.toJsonArray())
            .put("worldSettings", worldSettings.toJsonArray())
            .put("journals", journals.toJsonArray())
            .put("guides", guides.toJsonArray())
            .put("aiMemories", aiMemories.toJsonArray())

        return JSONObject()
            .put("type", "earth_ol_world_save")
            .put("schemaVersion", 1)
            .put("exportedAt", ContentConverter.nowText())
            .put("syncReady", true)
            .put("storage", "room_sqlite")
            .put("checksum", ContentConverter.checksum(data.toString()))
            .put("data", data)
            .toString(2)
    }

    fun decode(jsonText: String): WorldSaveData {
        val root = JSONObject(jsonText)
        require(root.optString("type") == "earth_ol_world_save") { "导入失败：不是地球OL完整世界存档" }
        val data = root.getJSONObject("data")

        return WorldSaveData(
            player = data.optJSONObject("player")?.toPlayerEntity(),
            tasks = data.optJSONArray("tasks").toList { it.toTaskEntity() },
            events = data.optJSONArray("events").toList { it.toEventEntity() },
            skills = data.optJSONArray("skills").toList { it.toSkillEntity() },
            relationships = data.optJSONArray("relationships").toList { it.toRelationshipEntity() },
            inventory = data.optJSONArray("inventory").toList { it.toInventoryEntity() },
            worldSettings = data.optJSONArray("worldSettings").toList { it.toWorldSettingEntity() },
            journals = data.optJSONArray("journals").toList { it.toJournalEntity() },
            guides = data.optJSONArray("guides").toList { it.toGuideEntity() },
            aiMemories = data.optJSONArray("aiMemories").toList { it.toAiMemoryEntity() }
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

    private fun <T> JSONArray?.toList(mapper: (JSONObject) -> T): List<T> {
        val array = this ?: return emptyList()
        return buildList {
            for (index in 0 until array.length()) {
                add(mapper(array.getJSONObject(index)))
            }
        }
    }

    private fun JSONObject.nullableLong(name: String): Long? =
        if (isNull(name)) null else optLong(name)

    private fun JSONObject.nullableInt(name: String): Int? =
        if (isNull(name)) null else optInt(name)

    private fun JSONObject.nullableString(name: String): String? =
        if (isNull(name)) null else optString(name)

    private fun JSONObject.toPlayerEntity() = PlayerEntity(
        id = optLong("id", 1),
        name = optString("name"),
        title = optString("title"),
        level = optInt("level"),
        exp = optInt("exp"),
        cash = optInt("cash"),
        currentStageId = nullableLong("currentStageId"),
        currentStyleId = nullableLong("currentStyleId"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toTaskEntity() = TaskEntity(
        id = optLong("id"),
        title = optString("title"),
        description = optString("description"),
        type = optString("type"),
        status = optString("status", "todo"),
        expReward = optInt("expReward"),
        attributeName = nullableString("attributeName"),
        attributeReward = optInt("attributeReward"),
        projectId = nullableLong("projectId"),
        bossMaxHp = nullableInt("bossMaxHp"),
        bossCurrentHp = nullableInt("bossCurrentHp"),
        dueDate = nullableString("dueDate"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt"),
        completedAt = nullableLong("completedAt"),
        guideText = optString("guideText")
    )

    private fun JSONObject.toEventEntity() = EventEntity(
        id = optLong("id"),
        title = optString("title"),
        description = optString("description"),
        type = optString("type"),
        status = optString("status", "open"),
        effectText = optString("effectText"),
        occurredAt = optString("occurredAt"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toSkillEntity() = SkillEntity(
        id = optLong("id"),
        name = optString("name"),
        category = optString("category"),
        level = optInt("level"),
        exp = optInt("exp"),
        description = optString("description"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toRelationshipEntity() = RelationshipEntity(
        id = optLong("id"),
        name = optString("name"),
        role = optString("role"),
        closeness = optInt("closeness"),
        trust = optInt("trust"),
        energyEffect = optString("energyEffect"),
        notes = optString("notes"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toInventoryEntity() = InventoryEntity(
        id = optLong("id"),
        name = optString("name"),
        type = optString("type"),
        quantity = optInt("quantity"),
        valueScore = optInt("valueScore"),
        notes = optString("notes"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toWorldSettingEntity() = WorldSettingEntity(
        id = optLong("id"),
        key = optString("key"),
        title = optString("title"),
        category = optString("category"),
        content = optString("content"),
        version = optInt("version", 1),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toJournalEntity() = JournalEntity(
        id = optLong("id"),
        title = optString("title"),
        content = optString("content"),
        moodScore = optInt("moodScore"),
        importance = optInt("importance"),
        entryDate = optString("entryDate"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toGuideEntity() = GuideEntity(
        id = optLong("id"),
        title = optString("title"),
        subtitle = optString("subtitle"),
        category = optString("category"),
        content = optString("content"),
        relatedModule = optString("relatedModule"),
        orderIndex = optInt("orderIndex"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    private fun JSONObject.toAiMemoryEntity() = AiMemoryEntity(
        id = optLong("id"),
        title = optString("title"),
        content = optString("content"),
        memoryType = optString("memoryType", "note"),
        importance = optInt("importance", 3),
        source = optString("source", "local"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )
}
