package com.kamstercodes.vyla.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    GlassBox(
        modifier = modifier,
        transparency = 0.2f
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeFormat.format(time),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.W200, // Thinner, more elegant iOS look
                color = Color.White,
                letterSpacing = (-2).sp
            )
            Text(
                text = dateFormat.format(time).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun WeatherWidget(modifier: Modifier = Modifier) {
    GlassBox(
        modifier = modifier,
        transparency = 0.2f
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "24°",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
                Text(
                    text = "Partly Cloudy".uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun BatteryWidget(modifier: Modifier = Modifier) {
    GlassBox(
        modifier = modifier,
        transparency = 0.2f
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BatteryFull,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color(0xFF34C759) // iOS Green
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "85%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier.width(80.dp).height(6.dp),
                    color = Color(0xFF34C759),
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}
