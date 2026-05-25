package com.golden.earthol.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.theme.HudDanger
import com.golden.earthol.theme.HudSurface

fun taskTypeLabel(type: String) = when (type) {
    "main" -> "主线"
    "daily" -> "日常"
    "side" -> "支线"
    "boss" -> "Boss"
    else -> type
}

@Composable
fun TaskCard(task: TaskEntity, onComplete: () -> Unit, onDelete: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(HudSurface, RoundedCornerShape(8.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(task.title, Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(if (task.status == "done") "已完成" else "待办", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(taskTypeLabel(task.type)) })
            AssistChip(onClick = {}, label = { Text("+${task.expReward} EXP") })
            task.attributeName?.let { AssistChip(onClick = {}, label = { Text(it) }) }
        }
        if (task.type == "boss") {
            Text("${task.bossCurrentHp ?: 0} / ${task.bossMaxHp ?: 0} HP", color = HudDanger)
            HudProgressBar(((task.bossCurrentHp ?: 0).toFloat() / (task.bossMaxHp ?: 1)).coerceIn(0f, 1f), color = HudDanger)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onComplete, enabled = task.status != "done", modifier = Modifier.weight(1f)) {
                Text("完成任务")
            }
            onDelete?.let {
                OutlinedButton(onClick = it) { Text("删除") }
            }
        }
    }
}
