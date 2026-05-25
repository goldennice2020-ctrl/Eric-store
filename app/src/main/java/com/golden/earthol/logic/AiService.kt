package com.golden.earthol.logic

import com.golden.earthol.BuildConfig
import com.golden.earthol.data.entity.AiMemoryEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class AiStatChanges(
    val san: Int = 0,
    val stamina: Int = 0,
    val spirit: Int = 0,
    val hunger: Int = 0,
    val sleep: Int = 0,
    val pressure: Int = 0,
    val focus: Int = 0,
    val relationshipEnergy: Int = 0,
    val health: Int = 0,
    val money: Int = 0,
    val dopamine: Int = 0,
    val loneliness: Int = 0,
    val meaning: Int = 0,
    val actionPower: Int = 0
)

data class AiPlayerState(
    val player: PlayerEntity?,
    val status: SurvivalStatusEntity?
)

data class AiResponse(
    val text: String,
    val changes: AiStatChanges
)

object AiService {
    suspend fun sendToAi(
        message: String,
        playerState: AiPlayerState,
        recentMemories: List<AiMemoryEntity>
    ): AiResponse {
        val apiKey = BuildConfig.DEEPSEEK_API_KEY.trim()
        if (apiKey.isBlank()) {
            return localMock(message, playerState, recentMemories)
        }

        return runCatching {
            requestDeepSeek(apiKey, message, playerState, recentMemories)
        }.getOrElse {
            val fallback = localMock(message, playerState, recentMemories)
            fallback.copy(
                text = buildReply(
                    "远程观察链路暂不可用。",
                    "已切回本地记录模式。",
                    fallback.text
                )
            )
        }
    }

    private suspend fun localMock(
        message: String,
        playerState: AiPlayerState,
        recentMemories: List<AiMemoryEntity>
    ): AiResponse {
        delay((1_000L..1_800L).random())

        val text = message.trim()
        val lower = text.lowercase()
        val currentStress = playerState.status?.stress ?: 50
        val memoryHint = recentMemories.firstOrNull()?.title.orEmpty()

        return when {
            containsAny(lower, "骑车", "自行车", "跑步", "散步", "健身", "运动") -> AiResponse(
                text = buildReply(
                    "你完成了一次身体系统维护。",
                    "压力开始下降，行动惯性被轻微改写。",
                    "持续重复后，它会从事件变成体质。"
                ),
                changes = AiStatChanges(stamina = -3, pressure = -3, health = 3, dopamine = 1, actionPower = 1)
            )

            containsAny(lower, "熬夜", "睡不着", "失眠", "通宵") -> AiResponse(
                text = buildReply(
                    "休息模块出现异常占用。",
                    "精神系统的恢复效率正在降低。",
                    "长时间缺乏睡眠，会让明天的行动变得昂贵。"
                ),
                changes = AiStatChanges(san = -4, stamina = -5, sleep = -6, pressure = 3, focus = -3)
            )

            containsAny(lower, "焦虑", "压力", "烦", "崩溃", "难受", "累") -> AiResponse(
                text = buildReply(
                    "系统检测到压力噪声上升。",
                    "你仍在维持运行，但效率已经开始被情绪税扣除。",
                    if (currentStress > 70) "建议减少额外战线，保留最低可行动作。" else "记录本身已经构成一次轻微卸载。"
                ),
                changes = AiStatChanges(san = -2, pressure = 2, focus = -1, loneliness = 1)
            )

            containsAny(lower, "学习", "写", "做了", "完成", "计划", "整理", "推进") -> AiResponse(
                text = buildReply(
                    "行动被记录。",
                    "专注模块获得短暂校准，意义感出现微弱回升。",
                    "命运通常不是被想明白的，而是被连续执行出来的。"
                ),
                changes = AiStatChanges(focus = 3, meaning = 2, actionPower = 2, stamina = -2)
            )

            containsAny(lower, "朋友", "家人", "聊天", "见面", "关系") -> AiResponse(
                text = buildReply(
                    "关系网络发生一次有效触碰。",
                    "孤独感出现下降迹象。",
                    "人不是资源，但连接会改变生存成本。"
                ),
                changes = AiStatChanges(relationshipEnergy = 4, loneliness = -3, san = 1)
            )

            containsAny(lower, "花钱", "买", "收入", "赚钱", "工资") -> AiResponse(
                text = buildReply(
                    "资源流动已记录。",
                    "金钱系统的波动会影响安全感，但不会直接定义玩家价值。",
                    "继续观察你的交换模式。"
                ),
                changes = AiStatChanges(money = if (containsAny(lower, "收入", "赚钱", "工资")) 100 else -50, pressure = 1)
            )

            else -> AiResponse(
                text = buildReply(
                    "新的现实片段已进入日志。",
                    if (memoryHint.isNotBlank()) "它将和旧记忆「$memoryHint」发生微弱关联。" else "当前尚未形成稳定模式。",
                    "持续记录，会让系统逐渐看见你的命运惯性。"
                ),
                changes = AiStatChanges(san = 1, meaning = 1)
            )
        }
    }

    private suspend fun requestDeepSeek(
        apiKey: String,
        message: String,
        playerState: AiPlayerState,
        recentMemories: List<AiMemoryEntity>
    ): AiResponse = withContext(Dispatchers.IO) {
        val endpoint = "${BuildConfig.DEEPSEEK_BASE_URL.trimEnd('/')}/chat/completions"
        val body = JSONObject()
            .put("model", BuildConfig.DEEPSEEK_MODEL)
            .put("temperature", 0.4)
            .put("response_format", JSONObject().put("type", "json_object"))
            .put(
                "messages",
                JSONArray()
                    .put(JSONObject().put("role", "system").put("content", deepSeekSystemPrompt()))
                    .put(JSONObject().put("role", "user").put("content", deepSeekUserPrompt(message, playerState, recentMemories)))
            )

        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $apiKey")
        }

        try {
            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val responseText = readResponse(connection)
            if (connection.responseCode !in 200..299) {
                error("DeepSeek 请求失败：HTTP ${connection.responseCode} $responseText")
            }

            val content = JSONObject(responseText)
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                .orEmpty()

            parseDeepSeekResponse(content)
        } finally {
            connection.disconnect()
        }
    }

    private fun deepSeekSystemPrompt(): String =
        listOf(
            "你是《地球Online》的世界观察系统，不是普通聊天机器人。",
            "你用克制、冷静、略带人生模拟器感的中文回应玩家。",
            "不要使用 emoji，不要哈哈哈，不要客服腔，不要鸡汤。",
            "回复内容不要展示具体数值变化。",
            "你必须只返回一个 JSON 对象，不要 Markdown，不要代码块。",
            "JSON 格式：{\"reply\":\"给玩家看的短回复\",\"changes\":{\"san\":0,\"stamina\":0,\"spirit\":0,\"hunger\":0,\"sleep\":0,\"pressure\":0,\"focus\":0,\"relationshipEnergy\":0,\"health\":0,\"money\":0,\"dopamine\":0,\"loneliness\":0,\"meaning\":0,\"actionPower\":0}}",
            "changes 每个字段必须是 -5 到 5 之间的整数，money 可以是 -500 到 500。"
        ).joinToString("\n")

    private fun deepSeekUserPrompt(
        message: String,
        playerState: AiPlayerState,
        recentMemories: List<AiMemoryEntity>
    ): String {
        val status = playerState.status
        val memories = recentMemories.take(5).joinToString("\n") { "- ${it.title}：${it.content.take(80)}" }
        return listOf(
            "玩家输入：$message",
            "",
            "当前玩家：${playerState.player?.name.orEmpty().ifBlank { "未知玩家" }}",
            "当前状态：体力=${status?.energy ?: "未知"}，精神=${status?.mental ?: "未知"}，饥饿=${status?.hunger ?: "未知"}，睡眠=${status?.sleep ?: "未知"}，压力=${status?.stress ?: "未知"}，专注=${status?.focus ?: "未知"}，关系能量=${status?.relationshipEnergy ?: "未知"}",
            "最近记忆：",
            memories.ifBlank { "暂无" },
            "",
            "请根据玩家输入生成观察者回应，并给出后台数值变化。"
        ).joinToString("\n")
    }

    private fun parseDeepSeekResponse(content: String): AiResponse {
        val normalized = content
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val json = JSONObject(normalized)
        val changes = json.optJSONObject("changes") ?: JSONObject()
        return AiResponse(
            text = json.optString("reply").trim().ifBlank { "现实片段已记录。" },
            changes = AiStatChanges(
                san = changes.optInt("san", 0).coerceIn(-5, 5),
                stamina = changes.optInt("stamina", 0).coerceIn(-5, 5),
                spirit = changes.optInt("spirit", 0).coerceIn(-5, 5),
                hunger = changes.optInt("hunger", 0).coerceIn(-5, 5),
                sleep = changes.optInt("sleep", 0).coerceIn(-5, 5),
                pressure = changes.optInt("pressure", 0).coerceIn(-5, 5),
                focus = changes.optInt("focus", 0).coerceIn(-5, 5),
                relationshipEnergy = changes.optInt("relationshipEnergy", 0).coerceIn(-5, 5),
                health = changes.optInt("health", 0).coerceIn(-5, 5),
                money = changes.optInt("money", 0).coerceIn(-500, 500),
                dopamine = changes.optInt("dopamine", 0).coerceIn(-5, 5),
                loneliness = changes.optInt("loneliness", 0).coerceIn(-5, 5),
                meaning = changes.optInt("meaning", 0).coerceIn(-5, 5),
                actionPower = changes.optInt("actionPower", 0).coerceIn(-5, 5)
            )
        )
    }

    private fun readResponse(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }
        return BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean =
        keywords.any { text.contains(it) }

    private fun buildReply(vararg lines: String): String =
        lines.filter { it.isNotBlank() }.joinToString("\n")
}
