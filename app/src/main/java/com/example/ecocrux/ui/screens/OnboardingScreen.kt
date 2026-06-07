package com.example.ecocrux.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecocrux.data.AuthRepository
import com.example.ecocrux.data.IndianEvVehicles
import com.example.ecocrux.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    var currentStep by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val vehicleOptions = IndianEvVehicles.list

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Step indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepDot(isActive = currentStep == 0)
                Spacer(modifier = Modifier.width(8.dp))
                StepDot(isActive = currentStep == 1)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Step ${currentStep + 1} of 2",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Step content with crossfade
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 2 } togetherWith
                            fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 2 }
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    0 -> StepOneContent(
                        name = name,
                        onNameChange = { name = it },
                        username = username,
                        onUsernameChange = { username = it }
                    )
                    1 -> StepTwoContent(
                        vehicle = vehicle,
                        onVehicleChange = { vehicle = it },
                        vehicleOptions = vehicleOptions,
                        vehicleDropdownExpanded = vehicleDropdownExpanded,
                        onExpandedChange = { vehicleDropdownExpanded = it },
                        onVehicleSelected = {
                            vehicle = it
                            vehicleDropdownExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = AccentRed,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Action button
            Button(
                onClick = {
                    errorMessage = null
                    when (currentStep) {
                        0 -> {
                            if (name.isBlank()) {
                                errorMessage = "Please enter your name"
                            } else {
                                currentStep = 1
                            }
                        }
                        1 -> {
                            if (vehicle.isBlank()) {
                                errorMessage = "Please select a vehicle"
                            } else {
                                isLoading = true
                                coroutineScope.launch {
                                    val result = repository.updateUserMetadata(
                                        name = name.trim(),
                                        username = username.trim().ifBlank { name.trim().lowercase().replace(" ", "") },
                                        vehicle = vehicle
                                    )
                                    isLoading = false
                                    if (result.isSuccess) {
                                        onComplete()
                                    } else {
                                        val errStr = result.exceptionOrNull()?.message ?: "Unknown error"
                                        val cleanErr = if (errStr.contains("<html", ignoreCase = true)) "Network/Server Error" else errStr
                                        errorMessage = "Failed to save profile: $cleanErr".take(100)
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BgDarkNavy,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (currentStep == 0) "Continue" else "Get Started",
                        color = BgDarkNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (currentStep == 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BgDarkNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Back button on step 2
            if (currentStep > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { currentStep-- }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", color = TextSecondary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StepDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .width(if (isActive) 32.dp else 10.dp)
            .height(10.dp)
            .clip(CircleShape)
            .background(
                if (isActive) Brush.horizontalGradient(
                    listOf(AccentGreen, AccentGreen.copy(alpha = 0.7f))
                )
                else Brush.horizontalGradient(
                    listOf(BorderSlate, BorderSlate)
                )
            )
    )
}

@Composable
private fun StepOneContent(
    name: String,
    onNameChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Welcome icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = AccentGreen.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Let's get to know you",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us your name so we can personalise\nyour experience",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = BorderSlate,
                focusedLabelColor = AccentGreen,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = AccentGreen
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username (optional)") },
            leadingIcon = {
                Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = TextSecondary)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = BorderSlate,
                focusedLabelColor = AccentGreen,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = AccentGreen
            ),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepTwoContent(
    vehicle: String,
    onVehicleChange: (String) -> Unit,
    vehicleOptions: List<String>,
    vehicleDropdownExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onVehicleSelected: (String) -> Unit
) {
    val filteredOptions = remember(vehicle) {
        if (vehicle.isBlank()) {
            vehicleOptions
        } else {
            vehicleOptions.filter { it.contains(vehicle, ignoreCase = true) }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Vehicle icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = AccentBlue.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ElectricCar,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What do you drive?",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Search for your EV model in India",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Vehicle type searchable dropdown
        ExposedDropdownMenuBox(
            expanded = vehicleDropdownExpanded,
            onExpandedChange = { onExpandedChange(!vehicleDropdownExpanded) }
        ) {
            OutlinedTextField(
                value = vehicle,
                onValueChange = { 
                    onVehicleChange(it)
                    onExpandedChange(true)
                },
                placeholder = { Text("e.g. Tata Nexon EV") },
                label = { Text("Vehicle Model") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleDropdownExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderSlate,
                    focusedLabelColor = AccentBlue,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = vehicleDropdownExpanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                if (filteredOptions.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No vehicles found", color = TextSecondary) },
                        onClick = {}
                    )
                } else {
                    filteredOptions.take(6).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { 
                                onVehicleSelected(option) 
                                onVehicleChange(option)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Popular vehicle visual cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Tata Nexon EV", "MG ZS EV", "Ola S1 Pro (Scooter)").forEach { option ->
                val isSelected = vehicle == option
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) AccentBlue.copy(alpha = 0.15f) else SurfaceDarkBlue,
                    border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
                    onClick = { 
                        onVehicleSelected(option)
                        onVehicleChange(option)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            if (option.contains("Scooter")) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (isSelected) AccentBlue else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            option.replace(" (Scooter)", "").replace("Tata ", "").replace("MG ", ""),
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
