package com.example.ecocrux.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class VehicleStats(
    val batteryPercent: Int = 0,
    val rangeLeftKm: Int = 0,
    val cabinTempCelsius: Int = 0
)

class VehicleViewModel : ViewModel() {
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _vehicleStats = MutableStateFlow<VehicleStats?>(null)
    val vehicleStats: StateFlow<VehicleStats?> = _vehicleStats.asStateFlow()

    fun connectBluetooth() {
        if (_connectionState.value == ConnectionState.CONNECTED) return

        _connectionState.value = ConnectionState.CONNECTING
        viewModelScope.launch {
            // Simulate Bluetooth connection delay
            delay(2000)
            _connectionState.value = ConnectionState.CONNECTED
            // Simulate reading vehicle stats over Bluetooth
            _vehicleStats.value = VehicleStats(
                batteryPercent = 74,
                rangeLeftKm = 312,
                cabinTempCelsius = 23
            )
        }
    }

    fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
        _vehicleStats.value = null
    }
}
