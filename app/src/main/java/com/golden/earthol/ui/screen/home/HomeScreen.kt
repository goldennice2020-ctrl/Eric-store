package com.golden.earthol.ui.screen.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golden.earthol.logic.AiAdvisorConfig
import com.golden.earthol.logic.AiProvider
import com.golden.earthol.logic.GameLogic

private val HomeBackground = Color(0xFFFFFFFF)
private val HomePanel = Color(0xFFF7F7F7)
private val HomePanelSoft = Color(0xFFF4F4F4)
private val HomeUserBubble = Color(0xFF111111)
private val HomeAiBubble = Color(0xFFF1F1F1)
private val HomeBorder = Color(0xFFE4E4E4)
private val HomeText = Color(0xFF171717)
private val HomeMuted = Color(0xFF626262)
private val HomeSubtle = Color(0xFF8A8A8A)

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("earthol_ai_advisor", Context.MODE_PRIVATE) }
    var input by remember { mutableStateOf("") }

    fun send(config: AiAdvisorConfig? = null) {
        val text = input.trim()
        if (text.isNotEmpty()) {
            viewModel.sendMessage(text, config)
            input = ""
        }
    }

    fun gptConfig(): AiAdvisorConfig {
        val provider = AiProvider.ChatGPT
        return AiAdvisorConfig(
            provider = provider,
            apiKey = prefs.getString("openai_key", "").orEmpty(),
            baseUrl = provider.defaultBaseUrl,
            model = prefs.getString("openai_model", provider.defaultModel).orEmpty().ifBlank { provider.defaultModel }
        )
    }

    LaunchedEffect(messages.size, isAiThinking) {
        val extraLoadingRow = if (isAiThinking) 1 else 0
        val lastIndex = messages.size + extraLoadingRow - 1
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        HomeHeader(
            playerName = state.player?.name ?: "邱硕",
            level = state.player?.level ?: 1,
            exp = state.player?.exp ?: 0,
            combatPower = state.combatPower,
            title = state.player?.title ?: "现实玩家 / AI 产品 / 创造玩家",
            stage = state.currentStage?.name ?: "成为自己",
            style = state.currentStyle?.name ?: "创造玩家",
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message)
            }

            if (isAiThinking) {
                item(key = "ai-loading") {
                    ThinkingBubble()
                }
            }
        }

        InputBar(
            value = input,
            onValueChange = { input = it },
            onGpt = { send(gptConfig()) },
            onDeepSeek = { send() },
            enabled = !isAiThinking
        )
    }
}

@Composable
private fun HomeHeader(
    playerName: String,
    level: Int,
    exp: Int,
    combatPower: Int,
    title: String,
    stage: String,
    style: String
) {
    val progress = (exp.toFloat() / GameLogic.requiredExp(level)).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeBackground)
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("地球Online", color = HomeText, fontSize = 38.sp, fontWeight = FontWeight.Bold)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(HomePanel)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(playerName, color = HomeMuted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("已登录地球 26 年", color = HomeText, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "今日状态",
                color = HomeMuted,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(HomeBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFF6CAA5B))
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: HomeChatMessage) {
    val isUser = message.author == HomeMessageAuthor.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.82f else 0.88f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUser) HomeUserBubble else HomeAiBubble)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (!isUser) {
                Text(
                    text = "系统",
                    color = HomeSubtle,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = message.content,
                color = if (isUser) Color(0xFFF2F2F2) else HomeText,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ThinkingBubble() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(HomeAiBubble)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(HomeMuted)
            )
            Text("同步中", color = HomeMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun InputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onGpt: () -> Unit,
    onDeepSeek: () -> Unit,
    enabled: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HomeBackground)
                .padding(start = 14.dp, top = 8.dp, end = 14.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(HomePanelSoft)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .onFocusChanged { isFocused = it.isFocused },
                enabled = enabled,
                textStyle = TextStyle(color = HomeText, fontSize = 18.sp, lineHeight = 24.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onDeepSeek() }),
                decorationBox = { innerTextField ->
                    Box(Modifier.fillMaxSize()) {
                        if (value.isBlank() && !isFocused) {
                            Text("现实轨迹同步中......", color = HomeSubtle, fontSize = 18.sp)
                        }
                        innerTextField()
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                ModelCircleButton(ModelButtonStyle.Orbit, enabled && value.isNotBlank(), onGpt)
                Spacer(Modifier.width(10.dp))
                ModelCircleButton(ModelButtonStyle.Send, enabled && value.isNotBlank(), onDeepSeek)
            }
        }
    }
}

private enum class ModelButtonStyle {
    Orbit,
    Send
}

@Composable
private fun ModelCircleButton(style: ModelButtonStyle, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() },
        shape = CircleShape,
        color = if (enabled) Color(0xFF202020) else Color.Transparent,
        border = if (enabled) null else androidx.compose.foundation.BorderStroke(1.dp, HomeText)
    ) {
        ModelButtonMark(style, enabled)
    }
}

@Composable
private fun ModelButtonMark(style: ModelButtonStyle, enabled: Boolean) {
    val markColor = if (enabled) Color(0xFFFFFFFF) else HomeText
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = center
        val stroke = Stroke(width = 1.4.dp.toPx())
        when (style) {
            ModelButtonStyle.Orbit -> {
                drawCircle(markColor, radius = 6.2.dp.toPx(), center = center, style = stroke)
                drawCircle(markColor, radius = 2.dp.toPx(), center = center)
                drawCircle(markColor, radius = 1.8.dp.toPx(), center = center.copy(x = center.x + 7.2.dp.toPx(), y = center.y - 4.8.dp.toPx()))
            }

            ModelButtonStyle.Send -> {
                drawLine(
                    color = markColor,
                    start = center.copy(x = center.x, y = center.y + 7.dp.toPx()),
                    end = center.copy(x = center.x, y = center.y - 6.5.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = markColor,
                    start = center.copy(x = center.x, y = center.y - 6.5.dp.toPx()),
                    end = center.copy(x = center.x - 5.2.dp.toPx(), y = center.y - 1.3.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = markColor,
                    start = center.copy(x = center.x, y = center.y - 6.5.dp.toPx()),
                    end = center.copy(x = center.x + 5.2.dp.toPx(), y = center.y - 1.3.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
