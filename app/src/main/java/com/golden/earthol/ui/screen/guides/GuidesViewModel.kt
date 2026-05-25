package com.golden.earthol.ui.screen.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.GuideEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GuidesUiState(val category: String = "总攻略", val guides: List<GuideEntity> = emptyList())

class GuidesViewModel(private val repo: GameRepository) : ViewModel() {
    private val category = MutableStateFlow("总攻略")
    val uiState = combine(repo.guides, category) { guides, cat ->
        GuidesUiState(cat, guides.filter { it.category == cat })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GuidesUiState())

    fun setCategory(value: String) { category.value = value }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = GuidesViewModel(repo) as T
    }
}
