package com.example.ecocrux.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecocrux.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingStationsScreen() {
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
            Text("Charging stations", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F3E34), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("3 available", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search stations...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceDarkBlue,
                unfocusedContainerColor = SurfaceDarkBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF1E2F23), RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("NEARBY STATIONS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Station Cards
        StationCard(name = "Tata Power EV — MG Road", distance = "1.2 km · Open 24/7", status = "Available", statusColor = AccentGreen, price = "₹15/kWh · 50kW DC")
        Spacer(modifier = Modifier.height(12.dp))
        StationCard(name = "EESL Charger — Forum Mall", distance = "2.8 km · 6AM–10PM", status = "1 queued", statusColor = AccentAmber, price = "₹12/kWh · 22kW AC")
        Spacer(modifier = Modifier.height(12.dp))
        StationCard(name = "ChargeZone — Airport Rd", distance = "4.1 km · Open 24/7", status = "In use", statusColor = AccentRed, price = "~18 min wait")
    }
}

@Composable
fun StationCard(name: String, distance: String, status: String, statusColor: Color, price: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDarkBlue, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(distance, color = TextSecondary, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(AccentGreen, CircleShape))
                Box(modifier = Modifier.size(8.dp).background(AccentBlue, CircleShape))
                Box(modifier = Modifier.size(8.dp).background(AccentAmber, CircleShape))
            }
            Text(price, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
