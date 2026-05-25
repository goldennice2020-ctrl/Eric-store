package com.golden.earthol.ui.screen.relationships

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.RelationshipEntity
import com.golden.earthol.ui.component.SectionTitle
import com.golden.earthol.ui.component.StatCard

@Composable
fun RelationshipsScreen(viewModel: RelationshipsViewModel) {
    val relationships by viewModel.relationships.collectAsState()
    var editing by remember { mutableStateOf<RelationshipEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var closeness by remember { mutableStateOf("0") }
    var trust by remember { mutableStateOf("0") }
    var effect by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val canSave = name.isNotBlank() && role.isNotBlank()

    fun clearForm() {
        editing = null
        name = ""
        role = ""
        closeness = "0"
        trust = "0"
        effect = ""
        notes = ""
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionTitle("关系")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("人物姓名") },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("和人物的关系") },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            viewModel.saveRelationship(
                                editing = editing,
                                name = name,
                                role = role,
                                closeness = closeness.toIntOrNull() ?: 0,
                                trust = trust.toIntOrNull() ?: 0,
                                energyEffect = effect,
                                notes = notes
                            )
                            clearForm()
                        },
                        enabled = canSave
                    ) {
                        Text(if (editing == null) "新增" else "更新")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = closeness,
                        onValueChange = { closeness = it.filter(Char::isDigit).take(3) },
                        modifier = Modifier.weight(1f),
                        label = { Text("亲近") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = trust,
                        onValueChange = { trust = it.filter(Char::isDigit).take(3) },
                        modifier = Modifier.weight(1f),
                        label = { Text("信任") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = effect,
                    onValueChange = { effect = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("能量影响") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("备注") },
                    minLines = 2
                )
                if (editing != null) {
                    OutlinedButton(onClick = ::clearForm, modifier = Modifier.fillMaxWidth()) {
                        Text("取消编辑")
                    }
                }
            }
        }

        items(relationships, key = { "relationship-${it.id}" }) { relationship ->
            RelationshipCard(
                relationship = relationship,
                onEdit = {
                    editing = relationship
                    name = relationship.name
                    role = relationship.role
                    closeness = relationship.closeness.toString()
                    trust = relationship.trust.toString()
                    effect = relationship.energyEffect
                    notes = relationship.notes
                },
                onDelete = {
                    if (editing?.id == relationship.id) clearForm()
                    viewModel.deleteRelationship(relationship)
                }
            )
        }
    }
}

@Composable
private fun RelationshipCard(
    relationship: RelationshipEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val detail = buildString {
        append("关系：")
        append(relationship.role)
        append("\n亲近 ${relationship.closeness} / 信任 ${relationship.trust}")
        if (relationship.notes.isNotBlank()) {
            append("\n")
            append(relationship.notes)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(relationship.name, detail, relationship.energyEffect.ifBlank { "已记录" })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                Text("编辑")
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                Text("删除")
            }
        }
    }
}
