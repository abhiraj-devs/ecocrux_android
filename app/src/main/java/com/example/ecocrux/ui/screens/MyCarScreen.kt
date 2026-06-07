package com.example.ecocrux.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
fun MyCarScreen() {
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
            Text("My car", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F3E34), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Connected", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Info Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Tata Nexon EV Max", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("MH 12 EV 4321 · 2023", color = TextSecondary, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Status Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("74%", color = AccentGreen, fontSize = 64.sp, fontWeight = FontWeight.Light)
            Text("312 km range remaining", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(BorderSlate, RoundedCornerShape(4.dp))) {
                Box(modifier = Modifier.fillMaxWidth(0.74f).height(8.dp).background(AccentGreen, RoundedCornerShape(4.dp)))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("91%", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("SOH", color = TextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("29°C", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Temp", color = TextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("412", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Cycles", color = TextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("~2h", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Full charge", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("REMOTE CONTROLS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(icon = Icons.Default.Lock, label = "Lock", color = AccentBlue, bgColor = Color(0xFF162544))
            QuickActionButton(icon = Icons.Default.AcUnit, label = "Climate", color = AccentGreen, bgColor = Color(0xFF0D2A24))
            QuickActionButton(icon = Icons.AutoMirrored.Filled.VolumeUp, label = "Horn", color = AccentAmber, bgColor = Color(0xFF332616))
            QuickActionButton(icon = Icons.Default.LocationOn, label = "Locate", color = Color(0xFFA855F7), bgColor = Color(0xFF2E1A47))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("LIVE DIAGNOSTICS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        DiagnosticCard(icon = Icons.Default.Bolt, title = "Motor efficiency", subtitle = "92% · Normal range", status = "Good", statusColor = AccentGreen)
        Spacer(modifier = Modifier.height(12.dp))
        DiagnosticCard(icon = Icons.Default.Warning, title = "Tyre pressure FL", subtitle = "27 PSI · Low detected", status = "Alert", statusColor = AccentRed)
    }
}

@Composable
fun DiagnosticCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, status: String, statusColor: Color) {
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
            Text(subtitle, color = TextSecondary, fontSize = 14.sp)
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
