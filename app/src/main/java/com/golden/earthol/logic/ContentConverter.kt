package com.golden.earthol.logic

import com.golden.earthol.data.entity.LibraryContentEntity
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object ContentConverter {
    const val ContentType = "earth_online_content_pack"
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun nowText(): String = LocalDateTime.now().format(timeFormatter)

    fun timestampForFile(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss"))

    fun checksum(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun convertTextToJson(
        rawText: String,
        title: String = rawText.trim().take(20).ifBlank { "未命名资料" },
        category: String = "见闻",
        tags: List<String> = emptyList(),
        importance: Int = 3,
        pairId: String = newPairId()
    ): String {
        val createdAt = nowText()
        val module = JSONObject()
            .put("id", "module_${UUID.randomUUID()}")
            .put("category", category)
            .put("title", title)
            .put("content", rawText)
            .put("tags", JSONArray(tags))
            .put("importance", importance.coerceIn(1, 5))
            .put("originalText", rawText)

        return JSONObject()
            .put("type", ContentType)
            .put("version", 1)
            .put("pairId", pairId)
            .put("title", title)
            .put("author", "Golden")
            .put("createdAt", createdAt)
            .put("sourceType", "text_import")
            .put("rawTextChecksum", checksum(rawText))
            .put("modules", JSONArray().put(module))
            .toString(2)
    }

    fun validateContentJson(structuredJson: String): JSONObject {
        val json = try {
            JSONObject(structuredJson)
        } catch (error: Exception) {
            throw IllegalArgumentException("JSON 格式无效：${error.message}")
        }
        if (json.optString("type") != ContentType) {
            throw IllegalArgumentException("JSON schema 无效：type 必须是 $ContentType")
        }
        if (!json.has("modules") || json.optJSONArray("modules") == null) {
            throw IllegalArgumentException("JSON schema 无效：缺少 modules 数组")
        }
        return json
    }

    fun convertJsonToMarkdown(structuredJson: String): String {
        val json = validateContentJson(structuredJson)
        val modules = json.optJSONArray("modules") ?: JSONArray()
        val builder = StringBuilder()
        builder.appendLine("# ${json.optString("title", "未命名资料")}")
        builder.appendLine()
        builder.appendLine("pairId：${json.optString("pairId")}")
        builder.appendLine("创建时间：${json.optString("createdAt")}")
        builder.appendLine("来源：${json.optString("sourceType")}")
        builder.appendLine("版本：${json.optInt("version", 1)}")
        builder.appendLine("作者：${json.optString("author", "Golden")}")
        builder.appendLine("rawTextChecksum：${json.optString("rawTextChecksum")}")
        builder.appendLine()

        for (index in 0 until modules.length()) {
            val module = modules.optJSONObject(index) ?: continue
            builder.appendLine("## 分类：${module.optString("category", "见闻")}")
            builder.appendLine()
            builder.appendLine("### ${module.optString("title", "未命名模块")}")
            builder.appendLine("模块ID：${module.optString("id")}")
            builder.appendLine("内容：")
            builder.appendLine(module.optString("content"))
            builder.appendLine("标签：${module.optJSONArray("tags")?.joinValues().orEmpty()}")
            builder.appendLine("重要性：${module.optInt("importance", 3)}")
            builder.appendLine("原文片段：")
            builder.appendLine(module.optString("originalText"))
            builder.appendLine()
        }
        return builder.toString()
    }

    fun entityFromText(rawText: String, title: String, category: String, tags: String, importance: Int): LibraryContentEntity {
        val pairId = newPairId()
        val tagList = splitTags(tags)
        val structuredJson = convertTextToJson(rawText, title, category, tagList, importance, pairId)
        val readableText = convertJsonToMarkdown(structuredJson)
        val now = nowText()
        return LibraryContentEntity(
            pairId = pairId,
            title = title.ifBlank { rawText.trim().take(20).ifBlank { "未命名资料" } },
            category = category.ifBlank { "见闻" },
            tags = tagList.joinToString(","),
            importance = importance.coerceIn(1, 5),
            rawText = rawText,
            structuredJson = structuredJson,
            readableText = readableText,
            sourceType = "text_import",
            createdAt = now,
            updatedAt = now,
            checksum = checksum(rawText + structuredJson)
        )
    }

    fun entityFromJson(structuredJson: String): LibraryContentEntity {
        val json = validateContentJson(structuredJson)
        val readableText = convertJsonToMarkdown(structuredJson)
        val modules = json.optJSONArray("modules")
        val firstModule = modules?.optJSONObject(0)
        val now = nowText()
        val pairId = json.optString("pairId").ifBlank { newPairId() }
        return LibraryContentEntity(
            pairId = pairId,
            title = json.optString("title", firstModule?.optString("title") ?: "未命名资料"),
            category = firstModule?.optString("category", "见闻") ?: "见闻",
            tags = firstModule?.optJSONArray("tags")?.joinValues().orEmpty(),
            importance = firstModule?.optInt("importance", 3) ?: 3,
            rawText = readableText,
            structuredJson = structuredJson,
            readableText = readableText,
            sourceType = "json_import",
            createdAt = json.optString("createdAt").ifBlank { now },
            updatedAt = now,
            checksum = checksum(readableText + structuredJson)
        )
    }

    fun entityFromPair(rawText: String, structuredJson: String): LibraryContentEntity {
        val json = validateContentJson(structuredJson)
        val pairId = json.optString("pairId").ifBlank { newPairId() }
        val title = json.optString("title").ifBlank { rawText.trim().take(20).ifBlank { "未命名资料" } }
        val readableText = convertJsonToMarkdown(structuredJson)
        val modules = json.optJSONArray("modules")
        val firstModule = modules?.optJSONObject(0)
        val now = nowText()
        return LibraryContentEntity(
            pairId = pairId,
            title = title,
            category = firstModule?.optString("category", "见闻") ?: "见闻",
            tags = firstModule?.optJSONArray("tags")?.joinValues().orEmpty(),
            importance = firstModule?.optInt("importance", 3) ?: 3,
            rawText = rawText,
            structuredJson = structuredJson,
            readableText = readableText,
            sourceType = "paired_import",
            createdAt = json.optString("createdAt").ifBlank { now },
            updatedAt = now,
            checksum = checksum(rawText + structuredJson)
        )
    }

    fun exportMarkdown(content: LibraryContentEntity, exportedAt: String = nowText()): String {
        val body = content.rawText?.takeIf { it.isNotBlank() }
            ?: content.structuredJson?.let { convertJsonToMarkdown(it) }
            ?: content.readableText.orEmpty()
        return buildString {
            appendLine("---")
            appendLine("pairId: ${content.pairId}")
            appendLine("version: ${content.version}")
            appendLine("exportedAt: $exportedAt")
            appendLine("sourceType: ${content.sourceType}")
            appendLine("checksum: ${content.checksum}")
            appendLine("---")
            appendLine()
            appendLine(body)
        }
    }

    fun exportJson(content: LibraryContentEntity, exportedAt: String = nowText()): String {
        val baseJson = content.structuredJson?.takeIf { it.isNotBlank() }
            ?: convertTextToJson(content.rawText.orEmpty(), content.title, content.category, splitTags(content.tags), content.importance, content.pairId)
        val json = validateContentJson(baseJson)
        json.put("pairId", content.pairId)
        json.put("version", content.version)
        json.put("exportedAt", exportedAt)
        json.put("sourceType", content.sourceType)
        json.put("checksum", content.checksum)
        return json.toString(2)
    }

    private fun newPairId(): String = "pair_${UUID.randomUUID()}"

    private fun splitTags(tags: String): List<String> =
        tags.split(",", "，", " ").map { it.trim() }.filter { it.isNotBlank() }

    private fun JSONArray.joinValues(): String {
        val values = mutableListOf<String>()
        for (index in 0 until length()) values += optString(index)
        return values.filter { it.isNotBlank() }.joinToString(",")
    }
}
