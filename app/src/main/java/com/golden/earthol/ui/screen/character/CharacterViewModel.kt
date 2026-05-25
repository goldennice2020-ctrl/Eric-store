package com.golden.earthol.ui.screen.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.logic.GameLogic
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CharacterUiState(
    val player: PlayerEntity? = null,
    val combatPower: Int = 0,
    val attributes: List<AttributeEntity> = emptyList(),
    val talents: List<TalentEntity> = emptyList(),
    val styles: List<PlayerStyleEntity> = emptyList(),
    val survivalStatus: SurvivalStatusEntity? = null,
    val currentStage: LifeStageEntity? = null,
    val currentStyle: PlayerStyleEntity? = null
)

class CharacterViewModel(private val repo: GameRepository) : ViewModel() {
    private val baseUiState = combine(repo.player, repo.attributes, repo.talents, repo.playerStyles, repo.lifeStages) { player, attrs, talents, styles, stages ->
            CharacterUiState(
                player = player,
                combatPower = GameLogic.combatPower(attrs, talents),
                attributes = attrs,
                talents = talents,
                styles = styles,
                currentStage = stages.firstOrNull { it.current },
                currentStyle = styles.firstOrNull { it.selected }
            )
        }

    val uiState = combine(baseUiState, repo.survivalStatus) { state, status ->
        state.copy(survivalStatus = status)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CharacterUiState())

    fun updateAttributeValue(attribute: AttributeEntity, value: Int) {
        viewModelScope.launch {
            repo.updateAttributeValue(attribute.id, value)
        }
    }

    fun updatePlayerProgress(level: Int, exp: Int) {
        viewModelScope.launch {
            repo.updatePlayerProgress(level, exp)
        }
    }

    fun addTalent(name: String, category: String, level: Int, description: String, unlocked: Boolean) {
        if (name.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            repo.addTalent(name, category, level, description, unlocked)
        }
    }

    fun deleteTalent(talent: TalentEntity) {
        viewModelScope.launch {
            repo.deleteTalent(talent)
        }
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CharacterViewModel(repo) as T
    }
}
