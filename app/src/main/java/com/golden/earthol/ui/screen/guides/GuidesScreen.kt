package com.golden.earthol.ui.screen.guides

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
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
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard

@Composable
fun GuidesScreen(viewModel: GuidesViewModel) {
    val state by viewModel.uiState.collectAsState()
    var selected by remember { mutableStateOf<GuideEntity?>(null) }
    val categories = listOf("总攻略", "首页攻略", "任务攻略", "角色攻略", "生存攻略", "天赋攻略", "项目攻略", "资产攻略", "地图攻略", "世界攻略", "隐藏任务攻略", "随机事件攻略", "人生阶段攻略", "玩家流派攻略", "档案攻略")

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionTitle("老玩家攻略")
            androidx.compose.foundation.layout.Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { FilterChip(selected = state.category == it, onClick = { viewModel.setCategory(it) }, label = { Text(it) }) }
            }
        }
        items(state.guides, key = { "guide-${it.id}" }) { guide ->
            StatCard(guide.title, guide.subtitle.ifBlank { guide.category }, guide.content.take(120) + "...", Modifier)
            OutlinedButton(onClick = { selected = guide }) { Text("进入详情") }
        }
    }

    selected?.let {
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(it.title) },
            text = { Text(it.content) },
            confirmButton = { OutlinedButton(onClick = { selected = null }) { Text("关闭") } }
        )
    }
}
