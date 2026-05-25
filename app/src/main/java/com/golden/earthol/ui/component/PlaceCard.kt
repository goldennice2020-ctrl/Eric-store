package com.golden.earthol.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.theme.HudSurface

fun placeTypeLabel(type: String) = when (type) {
    "work" -> "办公点"
    "recovery" -> "恢复点"
    "business" -> "商机点"
    "sport" -> "运动点"
    "life" -> "生活点"
    else -> type
}

@Composable
fun PlaceCard(place: PlaceEntity, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(HudSurface, RoundedCornerShape(8.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(place.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(placeTypeLabel(place.type))
        }
        Text("效率 ${place.efficiencyScore} / 恢复 ${place.recoveryScore} / 机会 ${place.opportunityScore} / 成本 ${place.costLevel}")
        if (place.notes.isNotBlank()) Text(place.notes)
        OutlinedButton(onClick = onDelete) { Text("删除地点") }
    }
}
