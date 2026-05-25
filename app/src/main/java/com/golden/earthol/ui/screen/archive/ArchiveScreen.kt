package com.golden.earthol.ui.screen.archive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.logic.ContentConverter
import com.golden.earthol.logic.SettingDocumentExportResult
import com.golden.earthol.logic.SettingDocumentFormat
import com.golden.earthol.ui.component.AssetCard
import com.golden.earthol.ui.component.PlaceCard
import com.golden.earthol.ui.component.ProjectCard
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard
import kotlinx.coroutines.launch

@Composable
fun ArchiveScreen(viewModel: ArchiveViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    var importMode by remember { mutableStateOf<String?>(null) }
    var rawText by remember { mutableStateOf("") }
    var jsonText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("见闻") }
    var tags by remember { mutableStateOf("") }
    var importance by remember { mutableIntStateOf(3) }
    var viewerTitle by remember { mutableStateOf<String?>(null) }
    var viewerText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showCompleteExportDialog by remember { mutableStateOf(false) }
    var showDatabaseDialog by remember { mutableStateOf(false) }
    var databaseJson by remember { mutableStateOf("") }
    var includeKnowledgeLayer by remember { mutableStateOf(false) }
    var lastSettingDocumentExport by remember { mutableStateOf<SettingDocumentExportResult?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val files = uris.mapNotNull { uri -> readImportFile(context, uri) }
        val textFile = files.firstOrNull { it.first.endsWith(".txt", true) || it.first.endsWith(".md", true) }
        val jsonFile = files.firstOrNull { it.first.endsWith(".json", true) }
        rawText = textFile?.second.orEmpty()
        jsonText = jsonFile?.second.orEmpty()
        title = rawText.trim().take(20).ifBlank { title }
        importMode = when {
            textFile != null && jsonFile != null -> "pair"
            textFile != null -> "text"
            jsonFile != null -> "json"
            else -> null
        }
        message = if (importMode == null) "导入失败：请选择 .txt / .md / .json 文件" else null
    }

    val worldSaveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.reader(Charsets.UTF_8).readText()
                }.orEmpty()
                viewModel.importWorldSave(context, json)
            }.onSuccess {
                message = "完整世界存档已导入恢复"
            }.onFailure {
                message = it.message ?: "导入失败：未知错误"
            }
        }
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionTitle("完整设定文档")
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    "地球OL完整设定文档",
                    "动态生成 / 核心功能",
                    "导出时实时读取当前数据库：世界观、价值观、规则、攻略、属性、状态、任务、地图、Buff/Debuff、人生阶段、用户导入资料和所有长期文本。"
                )
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { showCompleteExportDialog = true }, label = { Text("导出完整设定") })
                    AssistChip(onClick = {
                        scope.launch {
                            runCatching {
                                viewModel.copyCompleteSettingDocument(context, SettingDocumentFormat.Markdown)
                            }.onSuccess {
                                message = "完整设定文档已复制到剪贴板"
                            }.onFailure {
                                message = "导出失败：${it.message ?: "未知错误"}"
                            }
                        }
                    }, label = { Text("复制全文") })
                }
            }
        }

        item {
            SectionTitle("人生面板 / Player Layer")
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    "核心人生数据",
                    "默认可导出",
                    "包含属性、天赋、任务、日志、资产、地点、人生阶段、生存状态和行动记录。AI 分析玩家时默认只读取这一层。"
                )
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { showExportDialog = true }, label = { Text("导出玩家数据") })
                    AssistChip(onClick = {
                        scope.launch {
                            runCatching {
                                viewModel.exportWorldSave(context)
                            }.onSuccess {
                                message = "完整世界存档已导出：${it.absolutePath}"
                            }.onFailure {
                                message = "导出失败：${it.message ?: "未知错误"}"
                            }
                        }
                    }, label = { Text("导出世界存档 JSON") })
                    AssistChip(onClick = {
                        scope.launch {
                            runCatching {
                                viewModel.fullDatabaseJson()
                            }.onSuccess {
                                databaseJson = it
                                showDatabaseDialog = true
                            }.onFailure {
                                message = "读取数据库失败：${it.message ?: "未知错误"}"
                            }
                        }
                    }, label = { Text("查看完整数据库") })
                    AssistChip(onClick = { worldSaveLauncher.launch(arrayOf("application/json", "application/octet-stream")) }, label = { Text("导入世界存档 JSON") })
                }
            }
        }

        item {
            SectionTitle("世界数据库 / Knowledge Layer")
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    "外部知识层",
                    "默认只允许导入",
                    "攻略、世界观、价值观、经验包、城市观察、职场认知、AI 工具流等进入这里；不会覆盖玩家层。"
                )
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { fileLauncher.launch(arrayOf("text/plain", "text/markdown", "application/json", "application/octet-stream")) }, label = { Text("导入内容") })
                    AssistChip(onClick = { importMode = "text"; rawText = ""; jsonText = ""; title = ""; category = "见闻"; tags = ""; importance = 3 }, label = { Text("文字转 JSON") })
                    AssistChip(onClick = { importMode = "json"; rawText = ""; jsonText = ""; title = ""; category = "见闻"; tags = ""; importance = 3 }, label = { Text("JSON 转文字") })
                }
                message?.let { StatCard("系统提示", it, "导入默认追加；导入前自动备份，最近保留 7 个备份。资料库不会默认进入玩家数据导出。") }
            }
        }

        items(state.libraryContents, key = { "library-${it.id}" }) { content ->
            LibraryContentCard(
                content = content,
                onViewRaw = {
                    viewerTitle = "原文 / rawText"
                    viewerText = content.rawText.orEmpty()
                },
                onViewJson = {
                    viewerTitle = "结构化数据 / structuredJson"
                    viewerText = content.structuredJson.orEmpty()
                },
                onExport = {
                    val dir = viewModel.exportContent(context, content)
                    message = "已可选导出资料库条目到：${dir.absolutePath}"
                },
                onExportCategory = {
                    val dir = viewModel.exportAll(context, state.libraryContents.filter { it.category == content.category })
                    message = "已可选导出资料库分类「${content.category}」到：${dir.absolutePath}"
                }
            )
        }

        item { SectionTitle("项目档案") }
        items(state.projects, key = { "archive-project-${it.id}" }) { ProjectCard(it) }
        item { SectionTitle("资产档案") }
        items(state.assets, key = { "archive-asset-${it.id}" }) { AssetCard(it, onDelete = {}) }
        item { SectionTitle("地点档案") }
        items(state.places, key = { "archive-place-${it.id}" }) { PlaceCard(it, onDelete = {}) }
        item { SectionTitle("人生记录") }
        items(state.archives, key = { "archive-life-${it.id}" }) { StatCard("${it.date} / ${it.title}", it.type, "${it.content}\n情绪 ${it.emotionScore} / 重要 ${it.importanceScore}") }
        item {
            SectionTitle("年度日志")
            StatCard("年度日志入口", "第一版静态说明", "年度日志会记录：你经历了什么，完成了什么，失去了什么，发现了什么，仍然想完成什么。")
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("导出玩家数据") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("默认生成 player_data.json 与 player_data.md，只包含 Player Layer。")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Checkbox(checked = includeKnowledgeLayer, onCheckedChange = { includeKnowledgeLayer = it })
                        Text("同时导出资料库（默认关闭）")
                    }
                    Text("勾选后会额外生成 knowledge_layer.json 与 knowledge_layer.md。")
                }
            },
            confirmButton = {
                Button(onClick = {
                    val dir = viewModel.exportPlayerData(context, state, includeKnowledgeLayer)
                    message = if (includeKnowledgeLayer) {
                        "已导出 player_data 与 knowledge_layer 到：${dir.absolutePath}"
                    } else {
                        "已导出 player_data 到：${dir.absolutePath}"
                    }
                    showExportDialog = false
                    includeKnowledgeLayer = false
                }) {
                    Text("导出")
                }
            },
            dismissButton = { Button(onClick = { showExportDialog = false }) { Text("取消") } }
        )
    }

    if (showCompleteExportDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteExportDialog = false },
            title = { Text("导出完整设定文档") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("文档不是模板，会在导出瞬间读取当前数据库。")
                    Text("Markdown 文件名：地球OL_完整设定文档.md")
                    Text("TXT 文件名：地球OL_完整设定文档.txt")
                    lastSettingDocumentExport?.let {
                        Text("最近导出：${it.fileName}")
                    }
                }
            },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        scope.launch {
                            runCatching {
                                viewModel.exportCompleteSettingDocument(context, SettingDocumentFormat.Markdown)
                            }.onSuccess {
                                lastSettingDocumentExport = it
                                message = "完整设定文档已导出"
                            }.onFailure {
                                message = "导出失败：${it.message ?: "未知错误"}"
                            }
                        }
                    }) {
                        Text("保存 Markdown 到 Downloads")
                    }
                    Button(onClick = {
                        scope.launch {
                            runCatching {
                                viewModel.exportCompleteSettingDocument(context, SettingDocumentFormat.Txt)
                            }.onSuccess {
                                lastSettingDocumentExport = it
                                message = "完整设定文档已导出"
                            }.onFailure {
                                message = "导出失败：${it.message ?: "未知错误"}"
                            }
                        }
                    }) {
                        Text("保存 TXT 到 Downloads")
                    }
                    Button(
                        enabled = lastSettingDocumentExport != null,
                        onClick = {
                            lastSettingDocumentExport?.let { viewModel.shareCompleteSettingDocument(context, it) }
                        }
                    ) {
                        Text("系统分享 / 微信 / 文件管理器")
                    }
                }
            },
            dismissButton = { Button(onClick = { showCompleteExportDialog = false }) { Text("关闭") } }
        )
    }

    if (showDatabaseDialog) {
        AlertDialog(
            onDismissRequest = { showDatabaseDialog = false },
            title = { Text("完整数据库") },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 620.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("当前显示的是 Room 数据库实时导出的完整世界存档 JSON。")
                    Text(databaseJson)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("地球OL完整数据库", databaseJson))
                    message = "完整数据库已复制到剪贴板"
                }) {
                    Text("复制全部")
                }
            },
            dismissButton = {
                Button(onClick = { showDatabaseDialog = false }) { Text("关闭") }
            }
        )
    }

    if (importMode != null) {
        val structuredPreview = remember(rawText, jsonText, title, category, tags, importance, importMode) {
            runCatching {
                when (importMode) {
                    "json", "pair" -> jsonText.takeIf { it.isNotBlank() } ?: "{}"
                    else -> ContentConverter.convertTextToJson(
                        rawText,
                        title.ifBlank { rawText.trim().take(20).ifBlank { "未命名资料" } },
                        category.ifBlank { "见闻" },
                        tags.split(",", "，", " ").map { it.trim() }.filter { it.isNotBlank() },
                        importance
                    )
                }
            }.getOrElse { "预览失败：${it.message}" }
        }
        val markdownPreview = remember(structuredPreview) {
            runCatching { ContentConverter.convertJsonToMarkdown(structuredPreview) }.getOrElse { "Markdown 预览失败：${it.message}" }
        }

        AlertDialog(
            onDismissRequest = { importMode = null },
            title = { Text("导入预览") },
            text = {
                Column(Modifier.fillMaxWidth().heightIn(max = 560.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("pairId：${runCatching { org.json.JSONObject(structuredPreview).optString("pairId") }.getOrDefault("导入后生成")}")
                    Text("同名配对文件：${if (importMode == "pair") "已检测到" else "未检测到"}")
                    if (importMode != "json") {
                        OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题") })
                        OutlinedTextField(category, { category = it }, Modifier.fillMaxWidth(), label = { Text("分类") })
                        OutlinedTextField(tags, { tags = it }, Modifier.fillMaxWidth(), label = { Text("标签") })
                        OutlinedTextField(importance.toString(), { importance = it.toIntOrNull()?.coerceIn(1, 5) ?: importance }, Modifier.fillMaxWidth(), label = { Text("重要性 1-5") })
                    }
                    if (importMode != "json") OutlinedTextField(rawText, { rawText = it }, Modifier.fillMaxWidth().heightIn(min = 120.dp), label = { Text("原始文字 rawText") })
                    if (importMode != "text") OutlinedTextField(jsonText, { jsonText = it }, Modifier.fillMaxWidth().heightIn(min = 160.dp), label = { Text("结构化 JSON") })
                    PreviewBlock("结构化 JSON 预览", structuredPreview)
                    PreviewBlock("Markdown 预览", markdownPreview)
                }
            },
            confirmButton = {
                Button(onClick = {
                    runCatching {
                        when (importMode) {
                            "json" -> viewModel.importJson(context, jsonText)
                            "pair" -> viewModel.importPair(context, rawText, jsonText)
                            else -> viewModel.importText(context, rawText, title, category, tags, importance)
                        }
                        importMode = null
                        message = "导入已追加保存，原文和 JSON 已通过 pairId 绑定。"
                    }.onFailure { message = it.message }
                }) {
                    Text("追加导入")
                }
            },
            dismissButton = {
                Button(onClick = { importMode = null }) { Text("取消") }
            }
        )
    }

    viewerTitle?.let { titleText ->
        AlertDialog(
            onDismissRequest = { viewerTitle = null },
            title = { Text(titleText) },
            text = { Text(viewerText, Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState())) },
            confirmButton = { Button(onClick = { viewerTitle = null }) { Text("关闭") } }
        )
    }
}

@Composable
private fun LibraryContentCard(
    content: LibraryContentEntity,
    onViewRaw: () -> Unit,
    onViewJson: () -> Unit,
    onExport: () -> Unit,
    onExportCategory: () -> Unit
) {
    StatCard(
        title = "${content.category} / ${content.title}",
        value = "重要性 ${content.importance}  pairId ${content.pairId.take(13)}...",
        subtitle = "sourceType：${content.sourceType}\nchecksum：${content.checksum.take(16)}..."
    )
    Row(Modifier.padding(top = 6.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = onViewRaw, label = { Text("查看原文") })
        AssistChip(onClick = onViewJson, label = { Text("查看结构化数据") })
        AssistChip(onClick = onExport, label = { Text("导出内容") })
        AssistChip(onClick = onExportCategory, label = { Text("导出分类") })
    }
}

@Composable
private fun PreviewBlock(title: String, body: String) {
    Text(title, style = MaterialTheme.typography.titleSmall)
    Text(body, Modifier.fillMaxWidth().heightIn(max = 180.dp).verticalScroll(rememberScrollState()))
}

private fun readImportFile(context: android.content.Context, uri: Uri): Pair<String, String>? {
    val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
    } ?: uri.lastPathSegment.orEmpty()
    if (!name.endsWith(".txt", true) && !name.endsWith(".md", true) && !name.endsWith(".json", true)) return null
    val text = context.contentResolver.openInputStream(uri)?.use { input ->
        input.reader(Charsets.UTF_8).readText()
    } ?: return null
    return name to text
}
