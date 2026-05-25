package com.golden.earthol.ui.screen.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golden.earthol.data.GameRepository
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasksUiState(
    val tab: String = "今日",
    val tasks: List<TaskEntity> = emptyList(),
    val projects: List<ProjectEntity> = emptyList()
)

class TasksViewModel(private val repo: GameRepository) : ViewModel() {
    private val tab = MutableStateFlow("今日")
    val uiState = combine(repo.tasks, repo.projects, tab) { tasks, projects, currentTab ->
        TasksUiState(currentTab, tasks.filterBy(currentTab), projects)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TasksUiState())

    fun setTab(value: String) { tab.value = value }
    fun complete(task: TaskEntity) = viewModelScope.launch { repo.completeTask(task) }

    private fun List<TaskEntity>.filterBy(tab: String): List<TaskEntity> = when (tab) {
        "主线" -> filter { it.type == "main" }
        "日常" -> filter { it.type == "daily" || it.type == "survival" }
        "支线" -> filter { it.type == "side" }
        "Boss" -> filter { it.type == "boss" }
        "隐藏" -> filter { it.type == "hidden" || it.status == "hidden" || it.status == "discovered" }
        else -> filter { it.status == "todo" && it.type != "boss" }.take(8)
    }

    class Factory(private val repo: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TasksViewModel(repo) as T
    }
}
