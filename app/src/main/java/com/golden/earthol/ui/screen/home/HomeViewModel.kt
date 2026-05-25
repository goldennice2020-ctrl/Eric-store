package com.golden.earthol.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.logic.AiPlayerState
import com.golden.earthol.logic.AiAdvisorClient
import com.golden.earthol.logic.AiAdvisorConfig
import com.golden.earthol.logic.AiService
import com.golden.earthol.logic.GameLogic
import com.golden.earthol.logic.SettingDocumentFormat
import com.golden.earthol.logic.SettingDocumentGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class HomeUiState(
    val player: PlayerEntity? = null,
    val status: SurvivalStatusEntity? = null,
    val combatPower: Int = 0,
    val strategyName: String = "",
    val strategyDescription: String = "",
    val strategyAdvice: String = "",
    val debuffs: List<com.golden.earthol.data.entity.DebuffEntity> = emptyList(),
    val mainTasks: List<TaskEntity> = emptyList(),
    val projects: List<ProjectEntity> = emptyList(),
    val currentStage: LifeStageEntity? = null,
    val currentStyle: PlayerStyleEntity? = null
)

enum class HomeMessageAuthor {
    User,
    Ai
}

data class HomeChatMessage(
    val id: Long,
    val author: HomeMessageAuthor,
    val content: String
)

class HomeViewModel(private val repo: GameRepository) : ViewModel() {
    private data class HomeCore(
        val player: PlayerEntity?,
        val status: SurvivalStatusEntity?,
        val attributes: List<AttributeEntity>,
        val talents: List<TalentEntity>
    )

    private data class HomeWorld(
        val tasks: List<TaskEntity>,
        val projects: List<ProjectEntity>,
        val stages: List<LifeStageEntity>,
        val styles: List<PlayerStyleEntity>
    )

    private val core = combine(repo.player, repo.survivalStatus, repo.attributes, repo.talents) { player, status, attributes, talents ->
        HomeCore(player, status, attributes, talents)
    }

    private val world = combine(repo.tasks, repo.projects, repo.lifeStages, repo.playerStyles) { tasks, projects, stages, styles ->
        HomeWorld(tasks, projects, stages, styles)
    }

    val uiState = combine(core, world) { core, world ->
        val safeStatus = core.status ?: com.golden.earthol.data.InitialData.survivalStatus()
        val strategy = GameLogic.todayStrategy(safeStatus)
        HomeUiState(
            player = core.player,
            status = safeStatus,
            combatPower = GameLogic.combatPower(core.attributes, core.talents),
            strategyName = strategy.name,
            strategyDescription = strategy.description,
            strategyAdvice = strategy.advice,
            debuffs = GameLogic.judgeDebuffs(safeStatus),
            mainTasks = world.tasks.filter { it.type == "main" && it.status == "todo" }.take(3),
            projects = world.projects.filter { it.status == "active" }.take(3),
            currentStage = world.stages.firstOrNull { it.current },
            currentStyle = world.styles.firstOrNull { it.selected }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    private val _messages = MutableStateFlow(emptyList<HomeChatMessage>())
    val messages: StateFlow<List<HomeChatMessage>> = _messages

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking

    fun complete(task: TaskEntity) = viewModelScope.launch { repo.completeTask(task) }

    fun updateStatus(status: SurvivalStatusEntity) = viewModelScope.launch {
        repo.updateSurvivalStatus(status)
    }

    fun recordToday(content: String) = viewModelScope.launch {
        val text = content.trim()
        if (text.isBlank()) return@launch
        repo.addJournalEntry(
            title = "今日记录 ${LocalDate.now()}",
            content = text,
            entryDate = LocalDate.now().toString()
        )
    }

    fun sendMessage(message: String, advisorConfig: AiAdvisorConfig? = null) = viewModelScope.launch {
        val text = message.trim()
        if (text.isBlank() || _isAiThinking.value) return@launch

        _messages.update {
            it + HomeChatMessage(
                id = System.nanoTime(),
                author = HomeMessageAuthor.User,
                content = text
            )
        }
        _isAiThinking.value = true

        try {
            if (advisorConfig == null) {
                val current = uiState.value
                val response = AiService.sendToAi(
                    message = text,
                    playerState = AiPlayerState(current.player, current.status),
                    recentMemories = repo.recentAiMemories(limit = 5)
                )

                repo.applyAiStatChanges(response.changes, text, response.text)
                appendAiMessage(response.text)
            } else {
                val answer = withContext(Dispatchers.IO) {
                    val snapshot = repo.currentSettingDocumentSnapshot()
                    val context = SettingDocumentGenerator.generateCurrentSettingDocument(
                        snapshot,
                        SettingDocumentFormat.Txt
                    )
                    AiAdvisorClient.ask(advisorConfig, "首页", context, text)
                }
                appendAiMessage("[${advisorConfig.provider.label}]\n$answer")
            }
        } catch (error: Throwable) {
            appendAiMessage("AI 调用失败：${error.message ?: "未知错误"}")
        } finally {
            _isAiThinking.value = false
        }
    }

    private fun appendAiMessage(content: String) {
        _messages.update {
            it + HomeChatMessage(
                id = System.nanoTime(),
                author = HomeMessageAuthor.Ai,
                content = content
            )
        }
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repo) as T
    }
}
