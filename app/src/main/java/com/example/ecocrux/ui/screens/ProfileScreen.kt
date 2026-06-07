package com.example.ecocrux.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ecocrux.data.AuthRepository
import com.example.ecocrux.data.SupabaseClient
import com.example.ecocrux.theme.AccentGreen
import com.example.ecocrux.theme.AccentRed
import com.example.ecocrux.theme.BgDarkNavy
import com.example.ecocrux.theme.BorderSlate
import com.example.ecocrux.theme.SurfaceDarkBlue
import com.example.ecocrux.theme.TextSecondary
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Hatchback") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val vehicleOptions = listOf("Hatchback", "Sedan", "SUV", "Truck", "Motorcycle")
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    // Load existing metadata on launch
    LaunchedEffect(Unit) {
        val user = SupabaseClient.client.auth.currentSessionOrNull()?.user
        val metadata = user?.userMetadata
        if (metadata != null) {
            val jsonMetadata = metadata.jsonObject
            name = jsonMetadata["name"]?.jsonPrimitive?.contentOrNull ?: ""
            username = jsonMetadata["username"]?.jsonPrimitive?.contentOrNull ?: ""
            vehicleType = jsonMetadata["vehicleType"]?.jsonPrimitive?.contentOrNull ?: "Hatchback"
            val uriString = jsonMetadata["profileImage"]?.jsonPrimitive?.contentOrNull
            if (uriString != null) {
                profileImageUri = Uri.parse(uriString)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
            // In a real app, upload this URI file to Supabase Storage, but for now we just persist the local URI.
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile & Settings", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SurfaceDarkBlue)
                .clickable {
                    launcher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Upload Picture",
                    tint = TextSecondary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap to change picture", color = TextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = BorderSlate,
                focusedLabelColor = AccentGreen,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = BorderSlate,
                focusedLabelColor = AccentGreen,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Type Dropdown
        ExposedDropdownMenuBox(
            expanded = vehicleDropdownExpanded,
            onExpandedChange = { vehicleDropdownExpanded = !vehicleDropdownExpanded }
        ) {
            OutlinedTextField(
                value = vehicleType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehicle Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = BorderSlate,
                    focusedLabelColor = AccentGreen,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = vehicleDropdownExpanded,
                onDismissRequest = { vehicleDropdownExpanded = false }
            ) {
                vehicleOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            vehicleType = option
                            vehicleDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    try {
                        SupabaseClient.client.auth.updateUser {
                            data = buildJsonObject {
                                put("name", name)
                                put("username", username)
                                put("vehicleType", vehicleType)
                                put("profileImage", profileImageUri?.toString() ?: "")
                            }
                        }
                        saveMessage = "Profile updated successfully!"
                    } catch (e: Exception) {
                        saveMessage = "Failed to update profile."
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = BgDarkNavy, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Changes", color = BgDarkNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        if (saveMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(saveMessage!!, color = if (saveMessage!!.contains("successfully")) AccentGreen else AccentRed, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Support Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@ecocrux.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Feedback / Bug Report")
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                },
            color = SurfaceDarkBlue,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Support & Feedback", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Report a bug or send feedback", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        authRepository.logout()
                        onSignOut()
                    }
                },
            color = Color(0xFF2E1317),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = AccentRed)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Sign Out", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
