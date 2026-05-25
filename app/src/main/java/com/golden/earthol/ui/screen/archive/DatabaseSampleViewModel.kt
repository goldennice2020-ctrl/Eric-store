package com.golden.earthol.ui.screen.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.AiMemoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Minimal MVVM sample for future modules:
 * UI -> ViewModel -> Repository -> DAO -> Room.
 */
class DatabaseSampleViewModel(private val repo: GameRepository) : ViewModel() {
    val aiMemories = repo.aiMemories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addAiMemory(title: String, content: String) {
        viewModelScope.launch {
            repo.upsertAiMemory(
                AiMemoryEntity(
                    title = title,
                    content = content,
                    memoryType = "sample",
                    source = "local_ui"
                )
            )
        }
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DatabaseSampleViewModel(repo) as T
    }
}
