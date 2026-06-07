package com.example.ecocrux.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecocrux.theme.EcocruxTheme
import com.example.ecocrux.theme.AccentGreen
import com.example.ecocrux.theme.BgDarkNavy
import com.example.ecocrux.theme.TextSecondary

@Preview(showBackground = true, device = "id:pixel_8")
@Composable
fun DashboardScreenPreview() {
    EcocruxTheme {
        DashboardScreen(onSignOut = {}, onNavigateToFeedback = {}, onNavigateToMap = {})
    }
}

@Composable
fun DashboardScreen(onSignOut: () -> Unit, onNavigateToFeedback: () -> Unit, onNavigateToMap: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permissions if needed
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BgDarkNavy,
                contentColor = Color.White
            ) {
                // Tab 1: Home
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGreen,
                        unselectedIconColor = TextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 2: Chargers
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EvStation, contentDescription = "Chargers") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGreen,
                        unselectedIconColor = TextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 3: Car/Battery
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ElectricCar, contentDescription = "My Car") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGreen,
                        unselectedIconColor = TextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 4: Route Planner
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Route, contentDescription = "Trip Planner") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGreen,
                        unselectedIconColor = TextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                // Tab 5: Profile/Service
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentGreen,
                        unselectedIconColor = TextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(onNavigateToMap = onNavigateToMap)
                1 -> ChargingStationsScreen()
                2 -> MyCarScreen()
                3 -> TripPlannerScreen()
                4 -> ProfileScreen(onSignOut = onSignOut, onNavigateToFeedback = onNavigateToFeedback)
            }
        }
    }
}
