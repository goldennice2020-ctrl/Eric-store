package com.golden.earthol.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.logic.AiAdvisorClient
import com.golden.earthol.logic.AiAdvisorConfig
import com.golden.earthol.logic.SettingDocumentFormat
import com.golden.earthol.logic.SettingDocumentGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AiAdvisorUiState(
    val loading: Boolean = false,
    val answer: String = "",
    val error: String? = null
)

class AiAdvisorViewModel(private val repo: GameRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AiAdvisorUiState())
    val uiState: StateFlow<AiAdvisorUiState> = mutableUiState.asStateFlow()

    fun ask(config: AiAdvisorConfig, moduleName: String, question: String) {
        mutableUiState.value = AiAdvisorUiState(loading = true)
        viewModelScope.launch {
            runCatching {
                val context = withContext(Dispatchers.IO) {
                    SettingDocumentGenerator.generateCurrentSettingDocument(
                        snapshot = repo.currentSettingDocumentSnapshot(),
                        format = SettingDocumentFormat.Txt
                    )
                }
                withContext(Dispatchers.IO) {
                    AiAdvisorClient.ask(config, moduleName, context, question)
                }
            }.onSuccess { answer ->
                mutableUiState.value = AiAdvisorUiState(answer = "[${config.provider.label}]\n$answer")
            }.onFailure { error ->
                mutableUiState.value = AiAdvisorUiState(error = error.message ?: "AI 攻略请求失败")
            }
        }
    }

    fun clear() {
        mutableUiState.value = AiAdvisorUiState()
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AiAdvisorViewModel(repo) as T
    }
}
