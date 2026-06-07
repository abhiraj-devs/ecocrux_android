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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecocrux.theme.*
import com.example.ecocrux.ui.main.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingStationsScreen(locationViewModel: LocationViewModel = viewModel()) {
    val context = LocalContext.current
    val currentCity by locationViewModel.currentCity.collectAsState()
    val stations by locationViewModel.stations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        locationViewModel.fetchLocation(context)
    }
    
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
            Column {
                Text("Charging stations", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Near ${currentCity ?: "you"}", color = AccentGreen, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F3E34), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("${stations.size} available", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it 
                locationViewModel.updateManualLocation(it)
            },
            placeholder = { Text("Search location...", color = TextSecondary) },
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
        if (stations.isEmpty()) {
            if (locationViewModel.isSearching.collectAsState().value) {
                CircularProgressIndicator(color = AccentGreen, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Text("No chargers found in this area. Try searching.", color = TextSecondary, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        } else {
            stations.forEach { station ->
                val distanceStr = String.format("%.1f km", station.distanceKm)
                StationCard(
                    name = station.name,
                    distance = "$distanceStr · Open 24/7",
                    status = "Available",
                    statusColor = AccentGreen,
                    price = "Public Charger"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
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
