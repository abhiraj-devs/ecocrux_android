package com.example.ecocrux.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecocrux.data.AuthRepository
import com.example.ecocrux.theme.*
import java.util.Calendar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecocrux.ui.main.VehicleViewModel
import com.example.ecocrux.ui.main.ConnectionState

@Composable
fun HomeScreen(
    vehicleViewModel: VehicleViewModel = viewModel(),
    onNavigateToMap: () -> Unit = {}
) {
    val repository = remember { AuthRepository() }
    val profile = remember { repository.getUserProfile() }
    val firstName = profile?.firstName ?: "User"
    val initial = profile?.initial ?: "?"
    
    val connectionState by vehicleViewModel.connectionState.collectAsState()
    val vehicleStats by vehicleViewModel.vehicleStats.collectAsState()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    var showNotifications by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy)
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(greeting, color = TextSecondary, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(firstName, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.WavingHand, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box {
                    IconButton(
                        onClick = { showNotifications = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(SurfaceDarkBlue, CircleShape)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextSecondary)
                    }
                    
                    DropdownMenu(
                        expanded = showNotifications,
                        onDismissRequest = { showNotifications = false },
                        modifier = Modifier.background(SurfaceDarkBlue)
                    ) {
                        DropdownMenuItem(
                            text = { Text("No new notifications", color = Color.White) },
                            onClick = { showNotifications = false },
                            leadingIcon = { Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = TextSecondary) }
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0F3E34), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, color = AccentGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Map Placeholder Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF1E2F23), RoundedCornerShape(16.dp))
                .clickable { onNavigateToMap() }
                .padding(16.dp)
        ) {
            // Lines simulating streets
            Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(Color(0xFF2A4031)).align(Alignment.CenterStart).offset(x = 60.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF2A4031)).align(Alignment.TopCenter).offset(y = 60.dp))

            // Markers
            Box(modifier = Modifier.align(Alignment.TopStart).offset(x = 50.dp, y = 40.dp).size(24.dp).background(AccentGreen, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
            Box(modifier = Modifier.align(Alignment.Center).offset(x = (-10).dp, y = (-10).dp).size(24.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
            Box(modifier = Modifier.align(Alignment.Center).offset(x = 10.dp, y = 30.dp).size(24.dp).background(AccentRed, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Box(modifier = Modifier.align(Alignment.CenterEnd).offset(x = -40.dp, y = 10.dp).size(24.dp).background(AccentBlue, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }

            // Live Map Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF105B3A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Live map", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Grid
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                modifier = Modifier.weight(1f), 
                title = if (connectionState == ConnectionState.CONNECTED && vehicleStats != null) "${vehicleStats!!.batteryPercent}%" else "--%", 
                subtitle = "Battery", 
                titleColor = AccentGreen
            )
            StatCard(
                modifier = Modifier.weight(1f), 
                title = if (connectionState == ConnectionState.CONNECTED && vehicleStats != null) "${vehicleStats!!.rangeLeftKm} km" else "-- km", 
                subtitle = "Range left", 
                titleColor = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "3", subtitle = "Chargers nearby", titleColor = Color.White)
            StatCard(
                modifier = Modifier.weight(1f), 
                title = if (connectionState == ConnectionState.CONNECTED && vehicleStats != null) "${vehicleStats!!.cabinTempCelsius}°C" else "--°C", 
                subtitle = "Cabin temp", 
                titleColor = AccentAmber
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        Text("QUICK ACTIONS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(icon = Icons.Default.EvStation, label = "Find\ncharger", color = AccentGreen, bgColor = Color(0xFF0D2A24))
            QuickActionButton(icon = Icons.Default.Route, label = "Plan\ntrip", color = AccentBlue, bgColor = Color(0xFF162544))
            QuickActionButton(icon = Icons.Default.Build, label = "Service", color = AccentAmber, bgColor = Color(0xFF332616))
            QuickActionButton(icon = Icons.Default.SmartToy, label = "AI\nassist", color = Color(0xFFA855F7), bgColor = Color(0xFF2E1A47))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, subtitle: String, titleColor: Color) {
    Column(
        modifier = modifier
            .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = titleColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(subtitle, color = TextSecondary, fontSize = 14.sp)
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, color: Color, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .height(100.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}
