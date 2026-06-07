package com.example.ecocrux.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.example.ecocrux.data.AuthRepository
import com.example.ecocrux.data.GeminiService
import com.example.ecocrux.data.TimelineItemData
import com.example.ecocrux.theme.*

@Composable
fun TripPlannerScreen() {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val userProfile = remember { authRepository.getUserProfile() }
    val vehicle = userProfile?.vehicle?.ifBlank { "Tata Nexon EV" } ?: "Tata Nexon EV"

    var fromLocation by remember { mutableStateOf("Kochi, Kerala") }
    var toLocation by remember { mutableStateOf("Munnar, Kerala") }
    var isLoading by remember { mutableStateOf(false) }
    var timelineItems by remember { mutableStateOf<List<TimelineItemData>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Trip planner", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Color(0xFF2E1A47), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI", color = Color(0xFFA855F7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route Input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = { Text("Start Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = BorderSlate,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentRed,
                    unfocusedBorderColor = BorderSlate,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (fromLocation.isNotBlank() && toLocation.isNotBlank()) {
                        isLoading = true
                        coroutineScope.launch {
                            timelineItems = GeminiService.planTrip(fromLocation, toLocation, vehicle)
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plan with Gemini AI", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (timelineItems.isNotEmpty()) {
            Text("AI TRIP TIMELINE", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Timeline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                timelineItems.forEachIndexed { index, item ->
                    val color = when (item.type) {
                        "start" -> AccentGreen
                        "charge" -> AccentBlue
                        "food" -> AccentAmber
                        "arrive" -> AccentRed
                        else -> Color.Gray
                    }
                    TimelineItem(
                        color = color,
                        title = item.title,
                        subtitle = item.subtitle,
                        time = item.time,
                        isLast = index == timelineItems.size - 1
                    )
                }
            }
        } else {
            // Placeholder text when empty
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Enter locations and tap 'Plan' to generate an AI route.", color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun TimelineItem(color: Color, title: String, subtitle: String? = null, time: String, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = if (isLast) 0.dp else 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(40.dp).background(BorderSlate).padding(vertical = 4.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, color = TextSecondary, fontSize = 14.sp)
            }
        }
        Text(time, color = TextSecondary, fontSize = 12.sp)
    }
}
