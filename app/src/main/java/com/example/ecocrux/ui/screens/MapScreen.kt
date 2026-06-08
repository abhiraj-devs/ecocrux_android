package com.example.ecocrux.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ecocrux.theme.BgDarkNavy
import com.example.ecocrux.theme.TextSecondary
import com.example.ecocrux.theme.AccentGreen
import com.example.ecocrux.theme.AccentBlue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecocrux.ui.main.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.ecocrux.ui.main.ChargingStation
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapProperties

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val stations by locationViewModel.stations.collectAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<ChargingStation?>(null) }

    val searchSuggestions by locationViewModel.searchSuggestions.collectAsState()
    val routePolyline by locationViewModel.routePolyline.collectAsState()

    val defaultLocation = LatLng(19.0760, 72.8777) // Mumbai default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(loc.first, loc.second), 14f)
            )
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(500)
            locationViewModel.getSearchSuggestions(searchQuery)
        }
    }

    LaunchedEffect(Unit) {
        locationViewModel.fetchLocation(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
            onMapClick = {
                selectedStation = null
                locationViewModel.clearRoute()
            }
        ) {
            currentLocation?.let { loc ->
                Marker(
                    state = MarkerState(position = LatLng(loc.first, loc.second)),
                    title = "My Location"
                )
            }

            stations.forEach { station ->
                val stationPos = LatLng(station.lat, station.lon)
                Marker(
                    state = MarkerState(position = stationPos),
                    title = station.name,
                    snippet = "${String.format("%.1f", station.distanceKm)} km\nTap again to select.",
                    onClick = {
                        selectedStation = station
                        false
                    }
                )
            }

            routePolyline?.let { points ->
                Polyline(
                    points = points,
                    color = AccentBlue,
                    width = 16f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(BgDarkNavy.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close Map",
                        tint = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search location...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDarkNavy.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (isSearching || searchSuggestions.isNotEmpty()) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AccentGreen)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = AccentGreen)
                        }
                    }
                )
            }
            
            // Search Suggestions
            if (searchSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDarkNavy, RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    searchSuggestions.forEach { suggestion ->
                        Text(
                            text = suggestion.description,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = suggestion.description
                                    locationViewModel.selectSearchSuggestion(suggestion.placeId)
                                }
                                .padding(12.dp)
                        )
                        HorizontalDivider(color = Color(0xFF2A4031))
                    }
                }
            }
        }
        
        // Show Route Button
        if (selectedStation != null) {
            Button(
                onClick = {
                    currentLocation?.let { loc ->
                        locationViewModel.fetchRoute(loc.first, loc.second, selectedStation!!.lat, selectedStation!!.lon)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Show Route to ${selectedStation!!.name}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // FAB to center location
        FloatingActionButton(
            onClick = {
                currentLocation?.let { loc ->
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(LatLng(loc.first, loc.second), 14f)
                        )
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp),
            containerColor = AccentGreen,
            contentColor = BgDarkNavy
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        }
    }
}
