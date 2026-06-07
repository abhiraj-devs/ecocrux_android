package com.example.ecocrux.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.math.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

data class ChargingStation(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val distanceKm: Double
)

class LocationViewModel : ViewModel() {
    private val _currentCity = MutableStateFlow<String?>("Locating...")
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _stations = MutableStateFlow<List<ChargingStation>>(emptyList())
    val stations: StateFlow<List<ChargingStation>> = _stations.asStateFlow()

    fun updateManualLocation(city: String) {
        _currentCity.value = city
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        _isSearching.value = true
        
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            _currentCity.value = "Permission Denied"
            _isSearching.value = false
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModelScope.launch {
                    val city = getCityName(context, location.latitude, location.longitude)
                    _currentCity.value = city ?: "Unknown Location"
                    _isSearching.value = false
                    fetchStations(location.latitude, location.longitude)
                }
            } else {
                // Fallback for emulator or no location
                _currentCity.value = "Mumbai (Simulated)"
                _isSearching.value = false
            }
        }.addOnFailureListener {
            _currentCity.value = "Location Error"
            _isSearching.value = false
        }
    }

    private suspend fun getCityName(context: Context, lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchStations(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 10km radius
                val query = "[out:json];node[\"amenity\"=\"charging_station\"](around:10000,$lat,$lon);out;"
                val url = URL("https://overpass-api.de/api/interpreter?data=${java.net.URLEncoder.encode(query, "UTF-8")}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = InputStreamReader(connection.inputStream).readText()
                    val jsonObject = JSONObject(response)
                    val elements = jsonObject.optJSONArray("elements") ?: return@launch
                    
                    val parsedStations = mutableListOf<ChargingStation>()
                    for (i in 0 until elements.length()) {
                        val node = elements.getJSONObject(i)
                        val nodeLat = node.getDouble("lat")
                        val nodeLon = node.getDouble("lon")
                        val tags = node.optJSONObject("tags")
                        val name = tags?.optString("name")?.takeIf { it.isNotBlank() } ?: tags?.optString("brand")?.takeIf { it.isNotBlank() } ?: "Public EV Charger"
                        
                        val distance = calculateDistanceKm(lat, lon, nodeLat, nodeLon)
                        parsedStations.add(ChargingStation(node.getLong("id"), name, nodeLat, nodeLon, distance))
                    }
                    
                    // Sort by distance and take top 10
                    _stations.value = parsedStations.sortedBy { it.distanceKm }.take(10)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
