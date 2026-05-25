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
import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.theme.HudSurface

fun assetTypeLabel(type: String) = when (type) {
    "cash" -> "现金资产"
    "project" -> "项目资产"
    "device" -> "设备资产"
    "skill" -> "技能资产"
    "channel" -> "渠道资产"
    "contact" -> "人脉资产"
    else -> type
}

@Composable
fun AssetCard(asset: AssetEntity, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(HudSurface, RoundedCornerShape(8.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(asset.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(assetTypeLabel(asset.type))
        }
        Text("价值 ${asset.valueScore} / 潜力 ${asset.potentialScore} / 维护 ${asset.maintenanceCost}")
        Text("下一步：${asset.nextAction}")
        OutlinedButton(onClick = onDelete) { Text("删除资产") }
    }
}
