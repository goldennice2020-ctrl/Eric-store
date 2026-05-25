package com.golden.earthol.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.golden.earthol.theme.HudPrimary
import com.golden.earthol.theme.HudSurfaceAlt

@Composable
fun HudProgressBar(progress: Float, modifier: Modifier = Modifier, color: Color = HudPrimary) {
    Box(modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(2.dp)).background(HudSurfaceAlt)) {
        Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(8.dp).background(color))
    }
}
