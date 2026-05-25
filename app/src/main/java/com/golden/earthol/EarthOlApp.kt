package com.golden.earthol

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.golden.earthol.data.GameRepository
import com.golden.earthol.ui.navigation.AppNavGraph

@Composable
fun EarthOlApp(repository: GameRepository) {
    var showWelcomeDialog by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(repository) {
        repository.initializeDefaultData()
    }

    if (showWelcomeDialog) {
        Dialog(
            onDismissRequest = { showWelcomeDialog = false },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFFFFFFF),
                border = BorderStroke(1.dp, Color(0xFFE1E1E1)),
                tonalElevation = 0.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    LoginGlobe()
                    Text(
                        "欢迎登录地球Online",
                        color = Color(0xFF626262),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "出生随机  无法重开",
                        color = Color(0xFF171717),
                        fontSize = 23.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "确认当前存档，继续推进现实主线",
                        color = Color(0xFF626262),
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { showWelcomeDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 2.dp),
                        shape = RoundedCornerShape(99.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6CAA5B),
                            contentColor = Color(0xFFFFFFFF)
                        )
                    ) {
                        Text("进入地球Online", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    AppNavGraph(repository)
}

@Composable
private fun LoginGlobe() {
    Canvas(modifier = Modifier.size(98.dp)) {
        val green = Color(0xFF7FBE73)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.36f
        val stroke = Stroke(width = 3f)

        drawCircle(green, radius = radius, center = center, style = stroke)
        drawLine(green, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), strokeWidth = 2.5f)
        drawLine(green, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), strokeWidth = 2.5f)
        drawOval(
            color = green,
            topLeft = Offset(center.x - radius * 0.45f, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 0.9f, radius * 2f),
            style = Stroke(width = 2.5f)
        )
        drawOval(
            color = green,
            topLeft = Offset(center.x - radius, center.y - radius * 0.45f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 0.9f),
            style = Stroke(width = 2.5f)
        )
        drawCircle(green, radius = 4f, center = Offset(center.x - radius - 22f, center.y + 30f))
        drawCircle(green, radius = 6f, center = Offset(center.x + radius + 24f, center.y - 10f))
        drawLine(green, Offset(center.x - radius - 8f, center.y + 24f), Offset(center.x - radius - 34f, center.y + 36f), strokeWidth = 1.5f)
        drawLine(green, Offset(center.x + radius + 8f, center.y - 6f), Offset(center.x + radius + 34f, center.y - 18f), strokeWidth = 1.5f)
        drawLine(green, Offset(20f, center.y - 20f), Offset(36f, center.y - 20f), strokeWidth = 2f)
        drawLine(green, Offset(28f, center.y - 28f), Offset(28f, center.y - 12f), strokeWidth = 2f)
        drawLine(green, Offset(size.width - 28f, center.y + 24f), Offset(size.width - 16f, center.y + 24f), strokeWidth = 2f)
        drawLine(green, Offset(size.width - 22f, center.y + 18f), Offset(size.width - 22f, center.y + 30f), strokeWidth = 2f)
    }
}
