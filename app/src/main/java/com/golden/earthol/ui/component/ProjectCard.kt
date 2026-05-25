package com.golden.earthol.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.theme.HudSurface

@Composable
fun ProjectCard(project: ProjectEntity, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(HudSurface, RoundedCornerShape(8.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(project.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("${project.progress}%")
        }
        Text(project.stage, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
        HudProgressBar(project.progress / 100f)
        Text("下一步：${project.nextAction}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}
