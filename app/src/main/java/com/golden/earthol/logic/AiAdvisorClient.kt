package com.golden.earthol.logic

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

enum class AiProvider(
    val label: String,
    val defaultBaseUrl: String,
    val defaultModel: String
) {
    DeepSeek("DeepSeek", "https://api.deepseek.com", "deepseek-chat"),
    ChatGPT("ChatGPT", "https://api.openai.com/v1", "gpt-4.1")
}

data class AiAdvisorConfig(
    val provider: AiProvider,
    val apiKey: String,
    val baseUrl: String,
    val model: String,
    val temperature: Double = 0.3
)

object AiAdvisorClient {
    fun ask(config: AiAdvisorConfig, moduleName: String, databaseContext: String, question: String): String {
        require(config.apiKey.isNotBlank()) { "请先填写 ${config.provider.label} API Key" }
        require(question.isNotBlank()) { "请先输入问题" }

        val endpoint = "${config.baseUrl.trimEnd('/')}/chat/completions"
        val body = JSONObject()
            .put("model", config.model)
            .put("temperature", config.temperature)
            .put(
                "messages",
                JSONArray()
                    .put(
                        JSONObject()
                            .put("role", "system")
                            .put(
                                "content",
                                listOf(
                                    "你是《地球OL》App 内置攻略 AI。",
                                    "你可以读取用户本地数据库导出的完整设定文档。",
                                    "回答必须基于数据库上下文，不要编造用户没有记录的信息。",
                                    "当前界面：$moduleName。",
                                    "请给出可执行、具体、符合当前角色状态的建议。"
                                ).joinToString("\n")
                            )
                    )
                    .put(
                        JSONObject()
                            .put("role", "user")
                            .put(
                                "content",
                                listOf(
                                    "下面是 App 当前数据库的完整实时上下文：",
                                    databaseContext,
                                    "",
                                    "我的问题：",
                                    question
                                ).joinToString("\n")
                            )
                    )
            )

        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer ${config.apiKey}")
        }

        return try {
            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val responseText = readResponse(connection)
            if (connection.responseCode !in 200..299) {
                error("${config.provider.label} 请求失败：HTTP ${connection.responseCode} $responseText")
            }

            JSONObject(responseText)
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?.trim()
                .orEmpty()
                .ifBlank { "${config.provider.label} 没有返回内容" }
        } finally {
            connection.disconnect()
        }
    }

    private fun readResponse(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }
        return BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
    }
}
