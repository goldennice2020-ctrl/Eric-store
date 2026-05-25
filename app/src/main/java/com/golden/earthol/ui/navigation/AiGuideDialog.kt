package com.golden.earthol.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.golden.earthol.logic.AiAdvisorConfig
import com.golden.earthol.logic.AiProvider

@Composable
fun AiGuideDialog(
    moduleName: String,
    viewModel: AiAdvisorViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("earthol_ai_advisor", Context.MODE_PRIVATE) }
    val state by viewModel.uiState.collectAsState()
    var provider by rememberSaveable { mutableStateOf(AiProvider.DeepSeek.name) }
    val selectedProvider = AiProvider.valueOf(provider)
    var deepSeekKey by rememberSaveable { mutableStateOf("") }
    var openAiKey by rememberSaveable { mutableStateOf("") }
    var deepSeekModel by rememberSaveable { mutableStateOf(AiProvider.DeepSeek.defaultModel) }
    var openAiModel by rememberSaveable { mutableStateOf(AiProvider.ChatGPT.defaultModel) }
    var question by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        deepSeekKey = prefs.getString("deepseek_key", "").orEmpty()
        openAiKey = prefs.getString("openai_key", "").orEmpty()
        deepSeekModel = prefs.getString("deepseek_model", AiProvider.DeepSeek.defaultModel).orEmpty()
        openAiModel = prefs.getString("openai_model", AiProvider.ChatGPT.defaultModel).orEmpty()
        provider = prefs.getString("provider", AiProvider.DeepSeek.name).orEmpty().ifBlank { AiProvider.DeepSeek.name }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 攻略 / $moduleName") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedProvider == AiProvider.DeepSeek,
                        onClick = { provider = AiProvider.DeepSeek.name },
                        label = { Text("DeepSeek") }
                    )
                    FilterChip(
                        selected = selectedProvider == AiProvider.ChatGPT,
                        onClick = { provider = AiProvider.ChatGPT.name },
                        label = { Text("ChatGPT") }
                    )
                }

                if (selectedProvider == AiProvider.DeepSeek) {
                    OutlinedTextField(deepSeekKey, { deepSeekKey = it }, Modifier.fillMaxWidth(), label = { Text("DeepSeek API Key") })
                    OutlinedTextField(deepSeekModel, { deepSeekModel = it }, Modifier.fillMaxWidth(), label = { Text("DeepSeek Model") })
                } else {
                    OutlinedTextField(openAiKey, { openAiKey = it }, Modifier.fillMaxWidth(), label = { Text("OpenAI API Key") })
                    OutlinedTextField(openAiModel, { openAiModel = it }, Modifier.fillMaxWidth(), label = { Text("OpenAI Model") })
                }

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                    label = { Text("问 AI 攻略") }
                )

                state.error?.let { Text("错误：$it") }
                if (state.loading) Text("正在读取数据库并生成建议...")
                if (state.answer.isNotBlank()) {
                    Text(state.answer, Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !state.loading,
                onClick = {
                    prefs.edit()
                        .putString("provider", provider)
                        .putString("deepseek_key", deepSeekKey)
                        .putString("openai_key", openAiKey)
                        .putString("deepseek_model", deepSeekModel)
                        .putString("openai_model", openAiModel)
                        .apply()
                    viewModel.ask(
                        config = AiAdvisorConfig(
                            provider = selectedProvider,
                            apiKey = if (selectedProvider == AiProvider.DeepSeek) deepSeekKey else openAiKey,
                            baseUrl = selectedProvider.defaultBaseUrl,
                            model = if (selectedProvider == AiProvider.DeepSeek) deepSeekModel else openAiModel
                        ),
                        moduleName = moduleName,
                        question = question
                    )
                }
            ) {
                Text("询问")
            }
        },
        dismissButton = {
            Button(onClick = {
                viewModel.clear()
                onDismiss()
            }) {
                Text("关闭")
            }
        }
    )
}
