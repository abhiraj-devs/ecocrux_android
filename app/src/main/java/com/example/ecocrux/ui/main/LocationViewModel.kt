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

data class SearchSuggestion(val description: String, val placeId: String)

class LocationViewModel : ViewModel() {
    private val _currentCity = MutableStateFlow<String?>("Locating...")
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _stations = MutableStateFlow<List<ChargingStation>>(emptyList())
    val stations: StateFlow<List<ChargingStation>> = _stations.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<SearchSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<SearchSuggestion>> = _searchSuggestions.asStateFlow()

    private val _routePolyline = MutableStateFlow<List<org.osmdroid.util.GeoPoint>?>(null)
    val routePolyline: StateFlow<List<org.osmdroid.util.GeoPoint>?> = _routePolyline.asStateFlow()

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
                    _currentLocation.value = Pair(location.latitude, location.longitude)
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
                val apiKey = com.example.ecocrux.BuildConfig.MAPS_API_KEY
                if (apiKey.isNotBlank() && apiKey != "YOUR_API_KEY_HERE") {
                    val urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lon&radius=10000&type=charging_station&key=$apiKey"
                    val url = URL(urlStr)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response = InputStreamReader(connection.inputStream).readText()
                        val jsonObject = JSONObject(response)
                        val results = jsonObject.optJSONArray("results") ?: return@launch
                        val parsedStations = mutableListOf<ChargingStation>()
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val name = place.optString("name", "Public EV Charger")
                            val loc = place.getJSONObject("geometry").getJSONObject("location")
                            val nodeLat = loc.getDouble("lat")
                            val nodeLon = loc.getDouble("lng")
                            val distance = calculateDistanceKm(lat, lon, nodeLat, nodeLon)
                            parsedStations.add(ChargingStation(name.hashCode().toLong(), name, nodeLat, nodeLon, distance))
                        }
                        _stations.value = parsedStations.sortedBy { it.distanceKm }.take(10)
                    }
                } else {
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
    
    fun searchStationsManual(lat: Double, lon: Double) {
        _currentLocation.value = Pair(lat, lon)
        fetchStations(lat, lon)
    }

    fun getSearchSuggestions(query: String) {
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = com.example.ecocrux.BuildConfig.MAPS_API_KEY
            if (apiKey.isNotBlank() && apiKey != "YOUR_API_KEY_HERE") {
                try {
                    val urlStr = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=${java.net.URLEncoder.encode(query, "UTF-8")}&key=$apiKey"
                    val url = URL(urlStr)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response = InputStreamReader(connection.inputStream).readText()
                        val jsonObject = JSONObject(response)
                        val predictions = jsonObject.optJSONArray("predictions") ?: return@launch
                        val suggestions = mutableListOf<SearchSuggestion>()
                        for (i in 0 until predictions.length()) {
                            val prediction = predictions.getJSONObject(i)
                            suggestions.add(
                                SearchSuggestion(
                                    prediction.getString("description"),
                                    prediction.getString("place_id")
                                )
                            )
                        }
                        _searchSuggestions.value = suggestions
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    fun selectSearchSuggestion(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = com.example.ecocrux.BuildConfig.MAPS_API_KEY
            if (apiKey.isNotBlank() && apiKey != "YOUR_API_KEY_HERE") {
                try {
                    val urlStr = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&fields=geometry&key=$apiKey"
                    val url = URL(urlStr)
                    val connection = url.openConnection() as HttpURLConnection
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response = InputStreamReader(connection.inputStream).readText()
                        val result = JSONObject(response).optJSONObject("result")
                        val loc = result?.optJSONObject("geometry")?.optJSONObject("location")
                        if (loc != null) {
                            val lat = loc.getDouble("lat")
                            val lng = loc.getDouble("lng")
                            _currentLocation.value = Pair(lat, lng)
                            fetchStations(lat, lng)
                            _searchSuggestions.value = emptyList() // clear suggestions
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    fun fetchRoute(startLat: Double, startLon: Double, endLat: Double, endLon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val urlStr = "http://router.project-osrm.org/route/v1/driving/$startLon,$startLat;$endLon,$endLat?overview=full&geometries=geojson"
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = InputStreamReader(connection.inputStream).readText()
                    val routes = JSONObject(response).optJSONArray("routes")
                    if (routes != null && routes.length() > 0) {
                        val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")
                        val points = mutableListOf<org.osmdroid.util.GeoPoint>()
                        for (i in 0 until coordinates.length()) {
                            val point = coordinates.getJSONArray(i)
                            points.add(org.osmdroid.util.GeoPoint(point.getDouble(1), point.getDouble(0)))
                        }
                        _routePolyline.value = points
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun clearRoute() {
        _routePolyline.value = null
    }
}
