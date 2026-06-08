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
import androidx.compose.ui.viewinterop.AndroidView
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

import org.osmdroid.views.overlay.Marker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecocrux.ui.main.LocationViewModel
import android.location.Geocoder
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import com.example.ecocrux.ui.main.ChargingStation

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val stations by locationViewModel.stations.collectAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var mapController: org.osmdroid.api.IMapController? by remember { mutableStateOf(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<ChargingStation?>(null) }

    val searchSuggestions by locationViewModel.searchSuggestions.collectAsState()
    val routePolyline by locationViewModel.routePolyline.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(500)
            locationViewModel.getSearchSuggestions(searchQuery)
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        locationViewModel.fetchLocation(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // MapView inside AndroidView
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    mapController = controller
                    controller.setZoom(14.0)
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                
                // User Location Marker
                currentLocation?.let { loc ->
                    val userPoint = GeoPoint(loc.first, loc.second)
                    val userMarker = Marker(mapView)
                    userMarker.position = userPoint
                    userMarker.title = "My Location"
                    userMarker.icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                    mapView.overlays.add(userMarker)
                }

                // Station Markers
                stations.forEach { station ->
                    val stationPoint = GeoPoint(station.lat, station.lon)
                    val marker = Marker(mapView)
                    marker.position = stationPoint
                    marker.title = "${station.name} (${String.format("%.1f", station.distanceKm)} km)\nTap again to select."
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.setOnMarkerClickListener { m, _ ->
                        m.showInfoWindow()
                        selectedStation = station
                        true
                    }
                    mapView.overlays.add(marker)
                }

                // Route Polyline
                routePolyline?.let { points ->
                    val polyline = org.osmdroid.views.overlay.Polyline()
                    polyline.setPoints(points)
                    polyline.color = android.graphics.Color.parseColor("#3B82F6") // AccentBlue
                    polyline.width = 12f
                    mapView.overlays.add(polyline)
                }
                
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize().clickable { 
                selectedStation = null
                locationViewModel.clearRoute()
            }
        )

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
                    mapController?.animateTo(GeoPoint(loc.first, loc.second))
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp), // offset above bottom nav if present
            containerColor = AccentGreen,
            contentColor = BgDarkNavy
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        }
    }
}
