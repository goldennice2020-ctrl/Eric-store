package com.golden.earthol.ui.screen.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.logic.GameLogic
import com.golden.earthol.ui.component.AttributeCard
import com.golden.earthol.ui.component.HudProgressBar
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard

@Composable
fun CharacterScreen(viewModel: CharacterViewModel) {
    val state by viewModel.uiState.collectAsState()
    val player = state.player
    val playerLevel = player?.level ?: 1
    val playerExp = player?.exp ?: 0
    val relationshipEnergy = state.survivalStatus?.relationshipEnergy ?: 0
    var playerLevelText by remember { mutableStateOf(playerLevel.toString()) }
    var playerExpText by remember { mutableStateOf(playerExp.toString()) }
    var talentName by remember { mutableStateOf("") }
    var talentCategory by remember { mutableStateOf("") }
    var talentLevel by remember { mutableStateOf("1") }
    var talentDescription by remember { mutableStateOf("") }
    var talentUnlocked by remember { mutableStateOf(true) }
    val canAddTalent = talentName.isNotBlank() && talentCategory.isNotBlank()

    LaunchedEffect(playerLevel, playerExp) {
        playerLevelText = playerLevel.toString()
        playerExpText = playerExp.toString()
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionTitle("属性")
            CharacterSummaryCard(
                name = player?.name ?: "邱硕",
                level = playerLevel,
                relationshipEnergy = relationshipEnergy,
                styleName = state.currentStyle?.name ?: "创造玩家",
                stageName = state.currentStage?.name ?: "成为自己",
                modifier = Modifier.padding(top = 10.dp)
            )
            PlayerProgressEditor(
                levelText = playerLevelText,
                expText = playerExpText,
                requiredExp = GameLogic.requiredExp(playerLevel),
                onLevelChange = { input ->
                    playerLevelText = input.filter(Char::isDigit).take(3)
                    val level = playerLevelText.toIntOrNull() ?: return@PlayerProgressEditor
                    viewModel.updatePlayerProgress(level, playerExpText.toIntOrNull() ?: 0)
                },
                onExpChange = { input ->
                    playerExpText = input.filter(Char::isDigit).take(6)
                    val exp = playerExpText.toIntOrNull() ?: return@PlayerProgressEditor
                    viewModel.updatePlayerProgress(playerLevelText.toIntOrNull() ?: 1, exp)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            HudProgressBar(playerExp.toFloat() / GameLogic.requiredExp(playerLevel), Modifier.padding(top = 8.dp))
        }
        item { SectionTitle("属性面板") }
        items(state.attributes, key = { "attribute-${it.id}" }) { attribute ->
            AttributeCard(
                attribute = attribute,
                onValueChange = { value -> viewModel.updateAttributeValue(attribute, value) }
            )
        }
        item { SectionTitle("天赋树") }
        item {
            TalentEditor(
                name = talentName,
                onNameChange = { talentName = it },
                category = talentCategory,
                onCategoryChange = { talentCategory = it },
                level = talentLevel,
                onLevelChange = { talentLevel = it.filter(Char::isDigit).take(3) },
                description = talentDescription,
                onDescriptionChange = { talentDescription = it },
                unlocked = talentUnlocked,
                onUnlockedChange = { talentUnlocked = it },
                canSave = canAddTalent,
                onSave = {
                    viewModel.addTalent(
                        name = talentName,
                        category = talentCategory,
                        level = talentLevel.toIntOrNull() ?: 1,
                        description = talentDescription,
                        unlocked = talentUnlocked
                    )
                    talentName = ""
                    talentCategory = ""
                    talentLevel = "1"
                    talentDescription = ""
                    talentUnlocked = true
                }
            )
        }
        items(state.talents, key = { "talent-${it.id}" }) {
            TalentCard(
                talent = it,
                onDelete = { viewModel.deleteTalent(it) }
            )
        }
        item { SectionTitle("玩家流派") }
        items(state.styles, key = { "style-${it.id}" }) {
            StatCard("${if (it.selected) "当前：" else ""}${it.name}", it.focus, "Buff：${it.buff}\nDebuff：${it.debuff}\n攻略：${it.guideText}")
        }
    }
}

@Composable
private fun CharacterSummaryCard(
    name: String,
    level: Int,
    relationshipEnergy: Int,
    styleName: String,
    stageName: String,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text("关系能量 $relationshipEnergy / 100")
        HudProgressBar(relationshipEnergy / 100f, Modifier.padding(top = 6.dp, bottom = 10.dp))
        StatCard(
            title = name,
            value = "Lv.$level",
            subtitle = "当前流派：$styleName\n当前阶段：$stageName"
        )
    }
}

@Composable
private fun PlayerProgressEditor(
    levelText: String,
    expText: String,
    requiredExp: Int,
    onLevelChange: (String) -> Unit,
    onExpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = levelText,
                onValueChange = onLevelChange,
                modifier = Modifier.weight(0.65f),
                label = { Text("等级") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = expText,
                onValueChange = onExpChange,
                modifier = Modifier.weight(1f),
                label = { Text("EXP") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Text("EXP ${expText.ifBlank { "0" }} / $requiredExp")
    }
}

@Composable
private fun TalentEditor(
    name: String,
    onNameChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    level: String,
    onLevelChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    unlocked: Boolean,
    onUnlockedChange: (Boolean) -> Unit,
    canSave: Boolean,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                label = { Text("天赋名") },
                singleLine = true
            )
            OutlinedTextField(
                value = level,
                onValueChange = onLevelChange,
                modifier = Modifier.weight(0.45f),
                label = { Text("Lv") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        OutlinedTextField(
            value = category,
            onValueChange = onCategoryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("分类") },
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("描述") },
            minLines = 2
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.weight(1f)) {
                Checkbox(checked = unlocked, onCheckedChange = onUnlockedChange)
                Text("已解锁", modifier = Modifier.padding(top = 12.dp))
            }
            Button(onClick = onSave, enabled = canSave) {
                Text("新增")
            }
        }
    }
}

@Composable
private fun TalentCard(talent: TalentEntity, onDelete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(
            "${talent.category} / ${talent.name}",
            "Lv.${talent.level}  ${if (talent.unlocked) "已解锁" else "未解锁"}",
            talent.description
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = onDelete) {
                Text("删除")
            }
        }
    }
}
