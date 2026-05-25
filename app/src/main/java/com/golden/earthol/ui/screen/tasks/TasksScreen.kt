package com.golden.earthol.ui.screen.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.ui.component.HudProgressBar
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard
import com.golden.earthol.ui.component.TaskCard
import com.golden.earthol.ui.component.taskTypeLabel
import com.golden.earthol.theme.HudDanger

@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val state by viewModel.uiState.collectAsState()
    var selectedTask by remember { mutableStateOf<TaskEntity?>(null) }
    val tabs = listOf("今日", "主线", "日常", "支线", "Boss", "隐藏")

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionTitle("任务系统")
            androidx.compose.foundation.layout.Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    FilterChip(selected = state.tab == tab, onClick = { viewModel.setTab(tab) }, label = { Text(tab) })
                }
            }
        }
        items(state.tasks, key = { "task-${it.id}" }) { task ->
            if (task.type == "boss") {
                BossTaskCard(task, onGuide = { selectedTask = task }, onComplete = { viewModel.complete(task) })
            } else {
                TaskCard(task, onComplete = { viewModel.complete(task) })
                Button(onClick = { selectedTask = task }, modifier = Modifier.padding(top = 4.dp)) { Text("查看任务攻略") }
            }
        }
    }

    selectedTask?.let { task ->
        AlertDialog(
            onDismissRequest = { selectedTask = null },
            title = { Text(task.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("类型：${taskTypeLabel(task.type)}")
                    Text("奖励：${task.expReward} EXP")
                    task.attributeName?.let { Text("关联属性：$it +${task.attributeReward}") }
                    Text(task.description)
                    Text("攻略：${task.guideText}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.complete(task)
                    selectedTask = null
                }, enabled = task.status != "done") { Text("完成任务") }
            },
            dismissButton = { OutlinedButton(onClick = { selectedTask = null }) { Text("关闭") } }
        )
    }
}

@Composable
private fun BossTaskCard(task: TaskEntity, onGuide: () -> Unit, onComplete: () -> Unit) {
    val hp = task.bossCurrentHp ?: 0
    val maxHp = task.bossMaxHp ?: 1
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(task.title, "$hp / $maxHp HP", task.description)
        HudProgressBar(hp.toFloat() / maxHp, color = HudDanger)
        Button(onClick = onGuide) { Text("攻略") }
        Button(onClick = onComplete, enabled = task.status != "done") { Text("完成 Boss") }
    }
}
