package com.golden.earthol.ui.screen.relationships

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.RelationshipEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RelationshipsViewModel(private val repo: GameRepository) : ViewModel() {
    val relationships = repo.relationships
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addRelationship(name: String, role: String) {
        val trimmedName = name.trim()
        val trimmedRole = role.trim()
        if (trimmedName.isBlank() || trimmedRole.isBlank()) return

        viewModelScope.launch {
            repo.upsertRelationship(
                RelationshipEntity(
                    name = trimmedName,
                    role = trimmedRole
                )
            )
        }
    }

    fun saveRelationship(
        editing: RelationshipEntity?,
        name: String,
        role: String,
        closeness: Int,
        trust: Int,
        energyEffect: String,
        notes: String
    ) {
        val trimmedName = name.trim()
        val trimmedRole = role.trim()
        if (trimmedName.isBlank() || trimmedRole.isBlank()) return

        viewModelScope.launch {
            repo.upsertRelationship(
                (editing ?: RelationshipEntity(name = trimmedName, role = trimmedRole)).copy(
                    name = trimmedName,
                    role = trimmedRole,
                    closeness = closeness.coerceIn(0, 100),
                    trust = trust.coerceIn(0, 100),
                    energyEffect = energyEffect.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun deleteRelationship(relationship: RelationshipEntity) {
        viewModelScope.launch {
            repo.deleteRelationship(relationship)
        }
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = RelationshipsViewModel(repo) as T
    }
}
