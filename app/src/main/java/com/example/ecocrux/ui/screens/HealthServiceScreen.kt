package com.example.ecocrux.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun HealthServiceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Health & service", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Health Ring Card
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Simulated Ring
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 0.86f },
                    modifier = Modifier.fillMaxSize(),
                    color = AccentGreen,
                    trackColor = BorderSlate,
                    strokeWidth = 8.dp
                )
                Text("86%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column {
                HealthStatRow(label = "Overall health", value = "Good", valueColor = AccentGreen)
                HealthStatRow(label = "Battery SOH", value = "91%", valueColor = Color.White)
                HealthStatRow(label = "Next service", value = "23 days", valueColor = AccentAmber)
                HealthStatRow(label = "Open alerts", value = "2", valueColor = AccentRed)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("ACTIVE REMINDERS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        ReminderCard(icon = Icons.Default.Warning, title = "Tyre pressure FL", subtitle = "27 PSI · Needs refill", status = "Now", statusColor = AccentRed)
        Spacer(modifier = Modifier.height(12.dp))
        ReminderCard(icon = Icons.Default.Air, title = "Cabin air filter", subtitle = "14 months old", status = "Overdue", statusColor = AccentAmber)
        Spacer(modifier = Modifier.height(12.dp))
        ReminderCard(icon = Icons.Default.BatteryChargingFull, title = "Battery checkup", subtitle = "Due in 23 days", status = "Soon", statusColor = AccentAmber)
        Spacer(modifier = Modifier.height(12.dp))
        ReminderCard(icon = Icons.Default.SystemUpdate, title = "OTA update v3.4.1", subtitle = "Ready to install", status = "Ready", statusColor = AccentBlue)

        Spacer(modifier = Modifier.height(24.dp))
        // SOS Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E1317), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("SOS", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Emergency SOS", color = AccentRed, fontWeight = FontWeight.Bold)
                    Text("Tap to request roadside help", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun HealthStatRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 14.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ReminderCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(BorderSlate, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = statusColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = statusColor, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
