package com.kamstercodes.vyla.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf(Calendar.getInstance().time) }
    
    LaunchedEffect(Unit) {
        while (true) {
            time = Calendar.getInstance().time
            kotlinx.coroutines.delay(1000)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.3f),
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )

    GlassBox(
        modifier = modifier,
        transparency = 0.25f
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeFormat.format(time),
                style = TextStyle(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.W200,
                    shadow = textShadow
                ),
                color = Color.White,
                letterSpacing = (-2).sp
            )
            Text(
                text = dateFormat.format(time).uppercase(),
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = textShadow,
                    letterSpacing = 2.sp
                ),
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun WeatherWidget(modifier: Modifier = Modifier) {
    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.3f),
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )

    GlassBox(
        modifier = modifier,
        transparency = 0.25f
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "24°",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        shadow = textShadow
                    ),
                    color = Color.White
                )
                Text(
                    text = "Partly Cloudy".uppercase(),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        shadow = textShadow,
                        letterSpacing = 1.2.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun BatteryWidget(modifier: Modifier = Modifier) {
    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.3f),
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )

    GlassBox(
        modifier = modifier,
        transparency = 0.25f
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BatteryFull,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color(0xFF34C759) // iOS Green
            )
            Spacer(modifier = Modifier.width(18.dp))
            Column {
                Text(
                    text = "85%",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = textShadow
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier.width(90.dp).height(8.dp),
                    color = Color(0xFF34C759),
                    trackColor = Color.White.copy(alpha = 0.25f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}
