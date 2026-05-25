package com.golden.earthol.ui.screen.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.WorldRuleEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class WorldUiState(
    val worldView: String = "",
    val rules: List<WorldRuleEntity> = emptyList(),
    val stages: List<LifeStageEntity> = emptyList(),
    val places: List<PlaceEntity> = emptyList(),
    val events: List<RandomEventEntity> = emptyList(),
    val hiddenQuests: List<HiddenQuestEntity> = emptyList()
)

class WorldViewModel(repo: GameRepository) : ViewModel() {
    private data class WorldParts(
        val rules: List<WorldRuleEntity>,
        val stages: List<LifeStageEntity>,
        val places: List<PlaceEntity>,
        val events: List<RandomEventEntity>,
        val hiddenQuests: List<HiddenQuestEntity>
    )

    private val parts = combine(repo.worldRules, repo.lifeStages, repo.places, repo.randomEvents, repo.hiddenQuests) { rules, stages, places, events, hidden ->
        WorldParts(rules, stages, places, events, hidden)
    }

    val uiState = combine(repo.worldView, parts) { worldView, parts ->
        WorldUiState(
            worldView = worldView?.content.orEmpty(),
            rules = parts.rules,
            stages = parts.stages,
            places = parts.places,
            events = parts.events,
            hiddenQuests = parts.hiddenQuests
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorldUiState())

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = WorldViewModel(repo) as T
    }
}
