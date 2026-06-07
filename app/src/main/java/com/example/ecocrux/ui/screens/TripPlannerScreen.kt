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
import com.example.ecocrux.theme.*

@Composable
fun TripPlannerScreen() {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(AccentGreen, CircleShape))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Kochi, Kerala", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.padding(start = 3.dp).height(20.dp).width(2.dp).background(BorderSlate))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(AccentRed, CircleShape))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Munnar, Kerala", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Cards
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "130 km", subtitle = "Total distance", titleColor = Color.White)
            StatCard(modifier = Modifier.weight(1f), title = "2 stops", subtitle = "Charge stops", titleColor = AccentGreen)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("DAY 1 — KOCHI TO MUNNAR", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Timeline
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            TimelineItem(color = AccentGreen, title = "Start — Kochi (74% battery)", time = "8:00 AM", isLast = false)
            TimelineItem(color = AccentBlue, title = "Charge — Tata Power, Muvattupuzha", subtitle = "45 min · +40% · ₹95", time = "9:30 AM", isLast = false)
            TimelineItem(color = AccentAmber, title = "Lunch — Spice Garden Restaurant", subtitle = "Rated 4.4 · ₹₹", time = "10:20 AM", isLast = false)
            TimelineItem(color = AccentRed, title = "Arrive — Munnar (31% battery)", time = "1:00 PM", isLast = true)
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
