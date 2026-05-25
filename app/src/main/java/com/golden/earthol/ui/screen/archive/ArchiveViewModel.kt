package com.golden.earthol.ui.screen.archive

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.DebuffEntity
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.LifeArchiveEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.logic.ContentConverter
import com.golden.earthol.logic.SettingDocumentExportResult
import com.golden.earthol.logic.SettingDocumentExporter
import com.golden.earthol.logic.SettingDocumentFormat
import com.golden.earthol.logic.SettingDocumentGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class ArchiveUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val assets: List<AssetEntity> = emptyList(),
    val places: List<PlaceEntity> = emptyList(),
    val archives: List<LifeArchiveEntity> = emptyList(),
    val libraryContents: List<LibraryContentEntity> = emptyList(),
    val player: PlayerEntity? = null,
    val survivalStatus: SurvivalStatusEntity? = null,
    val attributes: List<AttributeEntity> = emptyList(),
    val talents: List<TalentEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val lifeStages: List<LifeStageEntity> = emptyList(),
    val playerStyles: List<PlayerStyleEntity> = emptyList(),
    val hiddenQuests: List<HiddenQuestEntity> = emptyList(),
    val randomEvents: List<RandomEventEntity> = emptyList(),
    val debuffs: List<DebuffEntity> = emptyList(),
    val message: String? = null
)

class ArchiveViewModel(private val repo: GameRepository) : ViewModel() {
    private data class PlayerCore(
        val player: PlayerEntity?,
        val survivalStatus: SurvivalStatusEntity?,
        val attributes: List<AttributeEntity>,
        val talents: List<TalentEntity>,
        val tasks: List<TaskEntity>
    )

    private data class PlayerWorld(
        val projects: List<ProjectEntity>,
        val assets: List<AssetEntity>,
        val places: List<PlaceEntity>,
        val archives: List<LifeArchiveEntity>,
        val libraryContents: List<LibraryContentEntity>
    )

    private data class PlayerMeta(
        val lifeStages: List<LifeStageEntity>,
        val playerStyles: List<PlayerStyleEntity>,
        val hiddenQuests: List<HiddenQuestEntity>,
        val randomEvents: List<RandomEventEntity>,
        val debuffs: List<DebuffEntity>
    )

    private val core: Flow<PlayerCore> = combine(repo.player, repo.survivalStatus, repo.attributes, repo.talents, repo.tasks) { player, status, attributes, talents, tasks ->
        PlayerCore(player, status, attributes, talents, tasks)
    }

    private val world: Flow<PlayerWorld> = combine(repo.projects, repo.assets, repo.places, repo.lifeArchives, repo.libraryContents) { projects, assets, places, archives, libraryContents ->
        PlayerWorld(projects, assets, places, archives, libraryContents)
    }

    private val meta: Flow<PlayerMeta> = combine(repo.lifeStages, repo.playerStyles, repo.hiddenQuests, repo.randomEvents, repo.debuffs) { lifeStages, playerStyles, hiddenQuests, randomEvents, debuffs ->
        PlayerMeta(lifeStages, playerStyles, hiddenQuests, randomEvents, debuffs)
    }

    val uiState = combine(core, world, meta) { core, world, meta ->
        ArchiveUiState(
            projects = world.projects,
            assets = world.assets,
            places = world.places,
            archives = world.archives,
            libraryContents = world.libraryContents,
            player = core.player,
            survivalStatus = core.survivalStatus,
            attributes = core.attributes,
            talents = core.talents,
            tasks = core.tasks,
            lifeStages = meta.lifeStages,
            playerStyles = meta.playerStyles,
            hiddenQuests = meta.hiddenQuests,
            randomEvents = meta.randomEvents,
            debuffs = meta.debuffs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ArchiveUiState())

    fun importText(context: Context, rawText: String, title: String, category: String, tags: String, importance: Int) {
        viewModelScope.launch {
            runCatching {
                require(rawText.isNotBlank()) { "导入失败：原始文字不能为空" }
                createBackup(context)
                repo.importLibraryContent(ContentConverter.entityFromText(rawText, title, category, tags, importance))
            }
        }
    }

    fun importJson(context: Context, structuredJson: String) {
        viewModelScope.launch {
            runCatching {
                createBackup(context)
                repo.importLibraryContent(ContentConverter.entityFromJson(structuredJson))
            }
        }
    }

    fun importPair(context: Context, rawText: String, structuredJson: String) {
        viewModelScope.launch {
            runCatching {
                require(rawText.isNotBlank()) { "导入失败：配对文字不能为空" }
                createBackup(context)
                repo.importLibraryContent(ContentConverter.entityFromPair(rawText, structuredJson))
            }
        }
    }

    fun exportContent(context: Context, content: LibraryContentEntity): File {
        val dir = exportDir(context)
        val baseName = "earthol_content_${ContentConverter.timestampForFile()}"
        File(dir, "$baseName.md").writeText(ContentConverter.exportMarkdown(content), Charsets.UTF_8)
        File(dir, "$baseName.json").writeText(ContentConverter.exportJson(content), Charsets.UTF_8)
        return dir
    }

    fun exportAll(context: Context, contents: List<LibraryContentEntity>): File {
        val dir = exportDir(context)
        contents.forEachIndexed { index, content ->
            val baseName = "earthol_content_${ContentConverter.timestampForFile()}_${index + 1}"
            File(dir, "$baseName.md").writeText(ContentConverter.exportMarkdown(content), Charsets.UTF_8)
            File(dir, "$baseName.json").writeText(ContentConverter.exportJson(content), Charsets.UTF_8)
        }
        return dir
    }

    fun exportPlayerData(context: Context, state: ArchiveUiState, includeKnowledgeLayer: Boolean): File {
        val dir = exportDir(context)
        val exportedAt = ContentConverter.nowText()
        val playerJson = buildPlayerLayerJson(state, exportedAt)
        File(dir, "player_data.json").writeText(playerJson.toString(2), Charsets.UTF_8)
        File(dir, "player_data.md").writeText(buildPlayerLayerMarkdown(state, playerJson, exportedAt), Charsets.UTF_8)

        if (includeKnowledgeLayer) {
            val knowledgeJson = buildKnowledgeLayerJson(state.libraryContents, exportedAt)
            File(dir, "knowledge_layer.json").writeText(knowledgeJson.toString(2), Charsets.UTF_8)
            File(dir, "knowledge_layer.md").writeText(buildKnowledgeLayerMarkdown(state.libraryContents, knowledgeJson, exportedAt), Charsets.UTF_8)
        }
        return dir
    }

    suspend fun exportWorldSave(context: Context): File {
        val dir = exportDir(context)
        val file = File(dir, "earthol_world_save_${ContentConverter.timestampForFile()}.json")
        file.writeText(repo.exportWorldSaveJson(), Charsets.UTF_8)
        return file
    }

    suspend fun fullDatabaseJson(): String = repo.exportFullDatabaseJson()

    suspend fun importWorldSave(context: Context, json: String) {
        require(json.isNotBlank()) { "导入失败：世界存档 JSON 不能为空" }
        createBackup(context)
        repo.importWorldSaveJson(json)
    }

    suspend fun generateCurrentSettingDocument(format: SettingDocumentFormat): String =
        SettingDocumentGenerator.generateCurrentSettingDocument(
            snapshot = repo.currentSettingDocumentSnapshot(),
            format = format
        )

    suspend fun exportCompleteSettingDocument(
        context: Context,
        format: SettingDocumentFormat
    ): SettingDocumentExportResult {
        val content = generateCurrentSettingDocument(format)
        return SettingDocumentExporter.saveToDownloads(context, content, format)
    }

    suspend fun copyCompleteSettingDocument(context: Context, format: SettingDocumentFormat) {
        val content = generateCurrentSettingDocument(format)
        SettingDocumentExporter.copyToClipboard(context, content)
    }

    fun shareCompleteSettingDocument(context: Context, result: SettingDocumentExportResult) {
        SettingDocumentExporter.share(context, result)
    }

    private suspend fun createBackup(context: Context) {
        val contents = repo.libraryContentsSnapshot()
        val backupDir = File(context.filesDir, "earthol_backups").apply { mkdirs() }
        val backup = File(backupDir, "earthol_backup_${ContentConverter.timestampForFile()}.json")
        val json = contents.joinToString(prefix = "[", postfix = "]") {
            ContentConverter.exportJson(it)
        }
        backup.writeText(json, Charsets.UTF_8)
        backupDir.listFiles()
            ?.filter { it.name.startsWith("earthol_backup_") }
            ?.sortedByDescending { it.lastModified() }
            ?.drop(7)
            ?.forEach { it.delete() }
    }

    private fun exportDir(context: Context): File =
        File(context.getExternalFilesDir(null) ?: context.filesDir, "earthol_exports").apply { mkdirs() }

    private fun buildPlayerLayerJson(state: ArchiveUiState, exportedAt: String): JSONObject {
        val data = JSONObject()
            .put("player", state.player?.toJsonObject())
            .put("survivalStatus", state.survivalStatus?.toJsonObject())
            .put("attributes", state.attributes.toJsonArray())
            .put("talents", state.talents.toJsonArray())
            .put("tasks", state.tasks.toJsonArray())
            .put("projects", state.projects.toJsonArray())
            .put("assets", state.assets.toJsonArray())
            .put("places", state.places.toJsonArray())
            .put("lifeArchives", state.archives.toJsonArray())
            .put("lifeStages", state.lifeStages.toJsonArray())
            .put("playerStyles", state.playerStyles.toJsonArray())
            .put("hiddenQuests", state.hiddenQuests.toJsonArray())
            .put("randomEvents", state.randomEvents.toJsonArray())
            .put("debuffs", state.debuffs.toJsonArray())
        return JSONObject()
            .put("type", "earth_online_player_layer")
            .put("version", 1)
            .put("exportedAt", exportedAt)
            .put("sourceType", "player_layer_export")
            .put("knowledgeLayerIncluded", false)
            .put("checksum", ContentConverter.checksum(data.toString()))
            .put("data", data)
    }

    private fun buildPlayerLayerMarkdown(state: ArchiveUiState, json: JSONObject, exportedAt: String): String =
        buildString {
            appendLine("# 地球Online 玩家层导出")
            appendLine()
            appendLine("version：1")
            appendLine("exportedAt：$exportedAt")
            appendLine("sourceType：player_layer_export")
            appendLine("checksum：${json.optString("checksum")}")
            appendLine()
            appendLine("## 玩家")
            appendLine("${state.player?.name.orEmpty()} / Lv.${state.player?.level ?: 1} / ${state.player?.title.orEmpty()}")
            appendLine()
            appendLine("## 生存状态")
            appendLine(state.survivalStatus?.summary.orEmpty())
            appendLine()
            appendLine("## 属性")
            state.attributes.forEach { appendLine("- ${it.name} Lv.${it.level} EXP ${it.exp} / ${it.category}") }
            appendLine()
            appendLine("## 天赋")
            state.talents.forEach { appendLine("- ${it.name} Lv.${it.level} / ${if (it.unlocked) "已解锁" else "未解锁"}") }
            appendLine()
            appendLine("## 任务")
            state.tasks.forEach { appendLine("- [${it.status}] ${it.title} / ${it.type}") }
            appendLine()
            appendLine("## 人生记录")
            state.archives.forEach { appendLine("- ${it.date} ${it.title}：${it.content}") }
        }

    private fun buildKnowledgeLayerJson(contents: List<LibraryContentEntity>, exportedAt: String): JSONObject {
        val data = contents.toJsonArray()
        return JSONObject()
            .put("type", "earth_online_knowledge_layer")
            .put("version", 1)
            .put("exportedAt", exportedAt)
            .put("sourceType", "knowledge_layer_optional_export")
            .put("checksum", ContentConverter.checksum(data.toString()))
            .put("contents", data)
    }

    private fun buildKnowledgeLayerMarkdown(contents: List<LibraryContentEntity>, json: JSONObject, exportedAt: String): String =
        buildString {
            appendLine("# 地球Online 世界数据库导出")
            appendLine()
            appendLine("version：1")
            appendLine("exportedAt：$exportedAt")
            appendLine("sourceType：knowledge_layer_optional_export")
            appendLine("checksum：${json.optString("checksum")}")
            appendLine()
            contents.forEach {
                appendLine("## ${it.category} / ${it.title}")
                appendLine("pairId：${it.pairId}")
                appendLine("sourceType：${it.sourceType}")
                appendLine("checksum：${it.checksum}")
                appendLine(it.rawText?.takeIf { raw -> raw.isNotBlank() } ?: it.readableText.orEmpty())
                appendLine()
            }
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

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ArchiveViewModel(repo) as T
    }
}
