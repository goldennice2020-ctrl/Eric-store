package com.golden.earthol.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.theme.HudMuted
import com.golden.earthol.theme.HudSurface
import com.golden.earthol.theme.HudSurfaceAlt
import com.golden.earthol.theme.HudText

@Composable
fun AttributeCard(
    attribute: AttributeEntity,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {}
) {
    val value = attribute.exp.coerceIn(0, 100)
    var valueText by remember(attribute.id) { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        valueText = value.toString()
    }

    Column(modifier.fillMaxWidth().background(HudSurface, RoundedCornerShape(8.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(attribute.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .width(78.dp)
                    .height(32.dp)
                    .background(HudSurfaceAlt, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = valueText,
                    onValueChange = { input ->
                        val digits = input.filter(Char::isDigit).take(3)
                        valueText = digits
                        digits.toIntOrNull()?.let { onValueChange(it.coerceIn(0, 100)) }
                    },
                    modifier = Modifier.width(30.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = HudText, fontSize = 16.sp, textAlign = TextAlign.End)
                )
                Text(" /100", color = HudMuted, fontSize = 13.sp)
            }
        }
        Box(Modifier.padding(top = 8.dp)) {
            HudProgressBar(value / 100f)
        }
    }
}
