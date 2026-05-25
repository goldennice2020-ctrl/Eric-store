package com.golden.earthol.ui.screen.world

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.golden.earthol.ui.component.PlaceCard
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard

@Composable
fun WorldScreen(viewModel: WorldViewModel) {
    val state by viewModel.uiState.collectAsState()
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionTitle("世界观总览")
            StatCard("欢迎来到地球 OL", "开放世界人生存档", state.worldView, Modifier.padding(top = 10.dp))
        }
        item { SectionTitle("世界规则") }
        items(state.rules, key = { "rule-${it.id}" }) { StatCard("${it.category} / ${it.title}", it.summary, it.content) }
        item { SectionTitle("人生阶段") }
        items(state.stages, key = { "stage-${it.id}" }) { StatCard("${if (it.current) "当前：" else ""}${it.name}", it.mainGoal, "警告：${it.warning}\n下一步：${it.nextAction}\n攻略：${it.guideText}") }
        item { SectionTitle("容器 / 地图") }
        items(state.places, key = { "place-${it.id}" }) { PlaceCard(it, onDelete = {}) }
        item { SectionTitle("随机事件") }
        items(state.events, key = { "event-${it.id}" }) { StatCard("${it.eventType} / ${it.title}", it.description, "A ${it.optionA}：${it.effectA}\nB ${it.optionB}：${it.effectB}\nC ${it.optionC}：${it.effectC}\n攻略：${it.guideText}") }
        item { SectionTitle("隐藏任务") }
        items(state.hiddenQuests, key = { "hidden-${it.id}" }) {
            if (it.status == "hidden") StatCard("？？？ 未发现的隐藏任务", it.triggerCondition, "继续探索世界。")
            else StatCard("${it.title}（${it.status}）", it.description, "奖励：${it.rewardText}\n攻略：${it.guideText}")
        }
    }
}
