package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Environment
import android.os.Build
import com.example.data.AvatarGeneration
import com.example.ui.theme.*
import com.example.ui.viewmodel.PersonaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainAppNavigation(viewModel: PersonaViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(color = Slate700.copy(alpha = 0.5f), thickness = 1.dp)
                NavigationBar(
                    containerColor = Slate950,
                    tonalElevation = 0.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Studio") },
                        label = { Text("Studio") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AccentIndigo,
                            indicatorColor = AccentIndigo.copy(alpha = 0.2f),
                            unselectedIconColor = Slate600,
                            unselectedTextColor = Slate600
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery") },
                        label = { Text("Gallery") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AccentIndigo,
                            indicatorColor = AccentIndigo.copy(alpha = 0.2f),
                            unselectedIconColor = Slate600,
                            unselectedTextColor = Slate600
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Scenes") },
                        label = { Text("Scenes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AccentIndigo,
                            indicatorColor = AccentIndigo.copy(alpha = 0.2f),
                            unselectedIconColor = Slate600,
                            unselectedTextColor = Slate600
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AccentIndigo,
                            indicatorColor = AccentIndigo.copy(alpha = 0.2f),
                            unselectedIconColor = Slate600,
                            unselectedTextColor = Slate600
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate900)
        ) {
            when (selectedTab) {
                0 -> GenerateScreen(viewModel)
                1 -> GalleryScreen(viewModel)
                2 -> ScenariosScreen(viewModel, onSelectScenario = { selectedTab = 0 })
                3 -> BrandKitScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(viewModel: PersonaViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val customFaces by viewModel.customFacesState.collectAsStateWithLifecycle()
    
    // Custom face image picker
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Prompt for custom face name
            Toast.makeText(context, "Adding custom face...", Toast.LENGTH_SHORT).show()
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val count = customFaces.size + 1
                    viewModel.addCustomFace(context, "Custom Persona #$count", inputStream)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Persona Studio",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            brush = Brush.linearGradient(listOf(AccentIndigo, AccentPurple))
                        )
                    )
                    Text(
                        text = "EXECUTIVE SUITE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        ),
                        color = Slate600
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Slate800, CircleShape)
                        .border(1.dp, Slate700.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 1. Reference Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Reference Portrait",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        IconButton(
                            onClick = { pickerLauncher.launch("image/*") },
                            modifier = Modifier.background(AccentIndigo, CircleShape)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add custom face", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Horizontal Avatars List
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Presets
                        items(viewModel.facePresets) { preset ->
                            val isSelected = viewModel.selectedFaceId == preset.id && viewModel.selectedCustomFaceId == null
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.selectFace(preset.id) }
                                    .width(80.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            brush = if (isSelected) {
                                                Brush.linearGradient(listOf(AccentPurple, AccentRose))
                                            } else {
                                                Brush.linearGradient(listOf(Slate600, Slate600))
                                            },
                                            shape = CircleShape
                                        )
                                        .background(Slate900),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PresetFaceThumbnail(presetId = preset.id, isSelected = isSelected)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.name.substringBefore(" "),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (isSelected) Color.White else Slate600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Customs
                        items(customFaces) { customFace ->
                            val isSelected = viewModel.selectedCustomFaceId == customFace.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.selectCustomFace(customFace.id) }
                                    .width(80.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            brush = if (isSelected) {
                                                Brush.linearGradient(listOf(AccentPurple, AccentRose))
                                            } else {
                                                Brush.linearGradient(listOf(Slate600, Slate600))
                                            },
                                            shape = CircleShape
                                        )
                                        .background(Slate900),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom Image loaded from base64
                                    val bitmap = remember(customFace.imageUri) {
                                        try {
                                            val decoded = Base64.decode(customFace.imageUri, Base64.DEFAULT)
                                            BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = customFace.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, contentDescription = customFace.name, tint = Slate600)
                                    }
                                    // Overlaid delete button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(Color.Red, CircleShape)
                                                .clickable { viewModel.deleteCustomFace(customFace.id) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(10.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = customFace.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (isSelected) Color.White else Slate600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Face Matching & Rendering Engine Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("matching_engine_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Face Matching & Rendering Engine",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose how your reference face is matched and rendered in the portrait.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate50.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Option 1: Designed Composition (Instant Local Blending)
                        val isComposition = viewModel.generationEngineMode == "Designed Composition"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isComposition) Slate700 else Slate900)
                                .border(
                                    width = if (isComposition) 2.dp else 1.dp,
                                    brush = if (isComposition) {
                                        Brush.linearGradient(listOf(AccentPurple, AccentRose))
                                    } else {
                                        Brush.linearGradient(listOf(Slate700, Slate700))
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectGenerationEngineMode("Designed Composition") }
                                .padding(12.dp)
                                .testTag("engine_designed_composition")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Brush,
                                    contentDescription = "Designed Composition",
                                    tint = if (isComposition) AccentRose else Slate600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Designed",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isComposition) Color.White else Slate50.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Instant local composition. Blends face with 100% precision, studio shadow, and ambient color lighting overlays.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isComposition) Slate100 else Slate600,
                                lineHeight = 14.sp
                            )
                        }

                        // Option 2: Gemini Multimodal AI Generation
                        val isGemini = viewModel.generationEngineMode == "Gemini Multimodal"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isGemini) Slate700 else Slate900)
                                .border(
                                    width = if (isGemini) 2.dp else 1.dp,
                                    brush = if (isGemini) {
                                        Brush.linearGradient(listOf(AccentPurple, AccentRose))
                                    } else {
                                        Brush.linearGradient(listOf(Slate700, Slate700))
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectGenerationEngineMode("Gemini Multimodal") }
                                .padding(12.dp)
                                .testTag("engine_gemini_multimodal")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini Multimodal",
                                    tint = if (isGemini) AccentPurple else Slate600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gemini AI",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isGemini) Color.White else Slate50.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Multimodal AI synthesis. Uses Gemini 2.5/3.1 flash image modality to generate custom scene variations.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isGemini) Slate100 else Slate600,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Body Type Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Proportions & Silhouette",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Custom Metrics",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (viewModel.isCustomMeasurementsEnabled) AccentRose else Slate100
                            )
                            Switch(
                                checked = viewModel.isCustomMeasurementsEnabled,
                                onCheckedChange = { viewModel.toggleCustomMeasurements(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentPurple,
                                    uncheckedThumbColor = Slate600,
                                    uncheckedTrackColor = Slate900
                                ),
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (!viewModel.isCustomMeasurementsEnabled) {
                        // Single Select Dropdown/Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(viewModel.bodyPresets) { body ->
                                val isSelected = viewModel.selectedBodyType == body
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectBodyType(body) },
                                    label = { Text(body) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentPurple,
                                        selectedLabelColor = Color.White,
                                        containerColor = Slate900,
                                        labelColor = Slate100
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = Slate600,
                                        selectedBorderColor = AccentRose
                                    )
                                )
                            }
                        }
                    } else {
                        // Custom Measurements Sliders
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate900, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Adjust Measurements (Inches)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AccentRose
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Bust Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Bust", style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                    Text("${viewModel.bustValue.toInt()}\"", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                                Slider(
                                    value = viewModel.bustValue,
                                    onValueChange = { viewModel.updateBust(it) },
                                    valueRange = 30f..50f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentRose,
                                        activeTrackColor = AccentRose,
                                        inactiveTrackColor = Slate700
                                    )
                                )
                            }

                            // Waist Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Waist", style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                    Text("${viewModel.waistValue.toInt()}\"", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                                Slider(
                                    value = viewModel.waistValue,
                                    onValueChange = { viewModel.updateWaist(it) },
                                    valueRange = 22f..45f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentPurple,
                                        activeTrackColor = AccentPurple,
                                        inactiveTrackColor = Slate700
                                    )
                                )
                            }

                            // Hips Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Hips", style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                    Text("${viewModel.hipsValue.toInt()}\"", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                                Slider(
                                    value = viewModel.hipsValue,
                                    onValueChange = { viewModel.updateHips(it) },
                                    valueRange = 30f..60f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentRose,
                                        activeTrackColor = AccentRose,
                                        inactiveTrackColor = Slate700
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Shape indicator card
                            val detectedShape = viewModel.calculateBodyShape(viewModel.bustValue, viewModel.waistValue, viewModel.hipsValue)
                            val explanation = viewModel.getBodyShapeExplanation(viewModel.bustValue, viewModel.waistValue, viewModel.hipsValue)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate800, shape = RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Shape Info",
                                            tint = AccentRose,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Detected Shape: $detectedShape",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate100,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Scene Selection & Description Box
        item {
            val scenario = viewModel.scenarios[viewModel.selectedScenarioIndex]
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Scene & Ambient Context",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Horizontal scrollable scenarios
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(viewModel.scenarios) { index, s ->
                            val isSelected = viewModel.selectedScenarioIndex == index && viewModel.customScenarioText.isEmpty()
                            Card(
                                onClick = { viewModel.selectScenario(index) },
                                modifier = Modifier.width(150.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Slate700 else Slate900
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    brush = if (isSelected) Brush.linearGradient(listOf(AccentPurple, AccentRose)) else Brush.linearGradient(listOf(Slate600, Slate600))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = s.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentRose
                                        )
                                        Icon(
                                            imageVector = when (s.iconName) {
                                                "business" -> Icons.Default.Work
                                                "brush" -> Icons.Default.Brush
                                                "terrain" -> Icons.Default.Terrain
                                                "directions_boat" -> Icons.Default.DirectionsBoat
                                                "electric_bolt" -> Icons.Default.ElectricBolt
                                                else -> Icons.Default.Place
                                            },
                                            contentDescription = s.name,
                                            tint = if (isSelected) Color.White else Slate600,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = s.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description Text Input with "Enhance with AI"
                    OutlinedTextField(
                        value = viewModel.customScenarioText.ifEmpty { scenario.prompt },
                        onValueChange = { viewModel.updateCustomScenarioText(it) },
                        label = { Text("Scenario Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Slate100,
                            focusedBorderColor = AccentPurple,
                            unfocusedBorderColor = Slate600,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        maxLines = 4,
                        trailingIcon = {
                            if (viewModel.isEnhancingScene) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentRose, strokeWidth = 2.dp)
                            } else {
                                IconButton(
                                    onClick = { viewModel.enhanceSceneDescription() },
                                    modifier = Modifier.background(AccentRose, RoundedCornerShape(8.dp))
                                ) {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = "Enhance with AI", tint = Color.White)
                                }
                            }
                        }
                    )
                    
                    viewModel.sceneEnhancementError?.let { err ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(err, color = Color.Red, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // 4. Style & Lighting Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Style & Art Direction",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Styles Horizontal
                    Text("Render Style", style = MaterialTheme.typography.labelMedium, color = Slate600)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.styles) { style ->
                            val isSelected = viewModel.selectedStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectStyle(style) },
                                label = { Text(style) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentIndigo,
                                    selectedLabelColor = Color.White,
                                    containerColor = Slate900,
                                    labelColor = Slate100
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Lighting Horizontal
                    Text("Ambient Lighting", style = MaterialTheme.typography.labelMedium, color = Slate600)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.lightings) { lighting ->
                            val isSelected = viewModel.selectedLighting == lighting
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectLighting(lighting) },
                                label = { Text(lighting) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentRose,
                                    selectedLabelColor = Color.White,
                                    containerColor = Slate900,
                                    labelColor = Slate100
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Slate600, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Strict Identity Preservation",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                "Locks facial geometry vs. artistic blend",
                                style = MaterialTheme.typography.labelMedium,
                                color = Slate600
                            )
                        }
                        Switch(
                            checked = viewModel.strictIdentityPreservation,
                            onCheckedChange = { viewModel.toggleIdentityPreservation(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentPurple,
                                checkedTrackColor = AccentPurple.copy(alpha = 0.4f),
                                uncheckedThumbColor = Slate600,
                                uncheckedTrackColor = Slate900
                            )
                        )
                    }
                }
            }
        }

        // 5. Prompt Live Preview Box
        item {
            val clipboard = LocalClipboardManager.current
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Constructed Prompt Preview",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AccentIndigo
                        )
                        IconButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(viewModel.livePrompt))
                                Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate100, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.livePrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate100
                    )
                }
            }
        }

        // 6. Generate Trigger
        item {
            var showResultDialog by remember { mutableStateOf(false) }
            
            Button(
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    viewModel.generatePortrait()
                    showResultDialog = true
                },
                enabled = !viewModel.isGeneratingImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("generate_portrait_button"),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(AccentIndigo, AccentPurple)),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✨",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE PORTRAIT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Result / Progress Dialog
            if (showResultDialog) {
                Dialog(onDismissRequest = { if (!viewModel.isGeneratingImage) showResultDialog = false }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "AI Synthesis Studio",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (viewModel.isGeneratingImage) {
                                CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    viewModel.generationProgressMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate100,
                                    textAlign = TextAlign.Center
                                )
                            } else if (viewModel.generationError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    viewModel.generationError ?: "An unexpected error occurred during rendering.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { showResultDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate600)
                                ) {
                                    Text("Dismiss")
                                }
                            } else {
                                // Result ready
                                val imageBytes = remember(viewModel.generatedImageResult) {
                                    try {
                                        Base64.decode(viewModel.generatedImageResult, Base64.DEFAULT)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                if (imageBytes != null) {
                                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Generated Portrait",
                                            modifier = Modifier
                                                .size(256.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .border(2.dp, AccentRose, RoundedCornerShape(16.dp))
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Generation Complete!",
                                    color = AccentEmerald,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showResultDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Gallery", maxLines = 1)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.generatedImageResult?.let { base64Str ->
                                                val success = saveImageToDownloads(context, base64Str, "Persona_Studio")
                                                if (success) {
                                                    Toast.makeText(context, "Portrait downloaded to Pictures/PersonaStudio!", Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to download portrait", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Download", maxLines = 1)
                                    }
                                    Button(
                                        onClick = {
                                            // Share support
                                            try {
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_SUBJECT, "My Persona Studio Portrait")
                                                    putExtra(Intent.EXTRA_TEXT, "Look at my premium professional avatar generated with Persona Studio:\n\n${viewModel.livePrompt}")
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Share Portrait"))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Sharing unavailable", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun GalleryScreen(viewModel: PersonaViewModel) {
    val generations by viewModel.generationsState.collectAsStateWithLifecycle()
    var selectedGenForZoom by remember { mutableStateOf<AvatarGeneration?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Studio History & Gallery",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Your persistent high-fidelity executive portfolios",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (generations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "No generations", tint = Slate600, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No portraits generated yet", color = Slate100, style = MaterialTheme.typography.titleMedium)
                    Text("Configure your inputs and trigger 'Generate' in first tab", color = Slate600, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(generations) { gen ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGenForZoom = gen }
                    ) {
                        Column {
                            Box(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                                // Load image from base64 string
                                val bitmap = remember(gen.imageUriOrBase64) {
                                    try {
                                        val decoded = Base64.decode(gen.imageUriOrBase64, Base64.DEFAULT)
                                        BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = gen.finalPrompt,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize().background(Slate900), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = Slate600)
                                    }
                                }

                                // Absolute overlays: Favorite and Delete
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .background(Slate900.copy(alpha = 0.8f), CircleShape)
                                                .size(28.dp)
                                                .clickable { viewModel.toggleFavorite(gen.id, gen.isFavorite) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (gen.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Favorite",
                                                tint = if (gen.isFavorite) Color.Red else Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Slate900.copy(alpha = 0.8f), CircleShape)
                                                .size(28.dp)
                                                .clickable { viewModel.deleteGeneration(gen.id) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = gen.faceName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = gen.scenarioName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AccentRose,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${gen.styleName} | ${gen.lightingName} | ${gen.bodyType}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Generation Time",
                                        tint = Slate600,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = formatTimestamp(gen.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Zoom Overlay Dialog
    selectedGenForZoom?.let { gen ->
        Dialog(onDismissRequest = { selectedGenForZoom = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                var scale by remember { mutableStateOf(1f) }
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        gen.faceName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        "${gen.scenarioName} • ${gen.bodyType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Generation Time",
                            tint = Slate600,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = formatTimestamp(gen.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Slate900)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 3f)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val bitmap = remember(gen.imageUriOrBase64) {
                            try {
                                val decoded = Base64.decode(gen.imageUriOrBase64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = gen.finalPrompt,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale
                                    ),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Pinch to zoom",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        gen.finalPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate100,
                        textAlign = TextAlign.Center,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val success = saveImageToDownloads(context, gen.imageUriOrBase64, "Persona_Studio")
                                if (success) {
                                    Toast.makeText(context, "Portrait downloaded to Pictures/PersonaStudio!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Failed to download portrait", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download", maxLines = 1)
                        }
                        Button(
                            onClick = {
                                viewModel.generateBrandKitFor(gen)
                                Toast.makeText(context, "Loaded brand kit data in Profile tab!", Toast.LENGTH_SHORT).show()
                                selectedGenForZoom = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CardMembership, contentDescription = "Brand Kit")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Brand Kit", maxLines = 1)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { selectedGenForZoom = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ScenariosScreen(viewModel: PersonaViewModel, onSelectScenario: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Elite Scenarios Hub",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Browse high-impact settings optimized for corporate & creative portraits",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(viewModel.scenarios) { index, s ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (s.iconName) {
                                        "business" -> Icons.Default.Work
                                        "brush" -> Icons.Default.Brush
                                        "terrain" -> Icons.Default.Terrain
                                        "directions_boat" -> Icons.Default.DirectionsBoat
                                        "electric_bolt" -> Icons.Default.ElectricBolt
                                        else -> Icons.Default.Place
                                    },
                                    contentDescription = s.name,
                                    tint = AccentRose,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    s.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Text(
                                s.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentIndigo,
                                modifier = Modifier
                                    .background(Slate900, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            s.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.selectScenario(index)
                                onSelectScenario()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Use Scenario")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandKitScreen(viewModel: PersonaViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeSubTab by remember { mutableStateOf(0) } // 0 = Brand Kit, 1 = Chat Advisor, 2 = API Settings
    var apiKeyVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI Brand Kit & Career Hub",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Maximize your professional footprint on LinkedIn & beyond",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tab switcher
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = Slate900,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = AccentPurple
                )
            }
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }) {
                Text("LinkedIn Kit", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }) {
                Text("Career Chat", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }) {
                Text("API Settings", modifier = Modifier.padding(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (activeSubTab) {
            0 -> {
                // LinkedIn Brand Kit
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (viewModel.brandKitGenerationId == null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CardMembership, contentDescription = null, tint = Slate600, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Portrait Selected", color = Slate100, style = MaterialTheme.typography.titleMedium)
                                    Text("Go to Gallery and select 'Create Brand Kit'", color = Slate600, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        item {
                            if (viewModel.isGeneratingBrandKit) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = AccentRose)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("AI is strategizing your brand assets...", color = Slate100)
                                    }
                                }
                            } else {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("LinkedIn Banner Proposal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                            IconButton(onClick = {
                                                clipboardManager.setText(AnnotatedString(viewModel.linkedinHeaderProposal))
                                                Toast.makeText(context, "Header proposal copied!", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate100)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(viewModel.linkedinHeaderProposal.ifEmpty { "Generates customized high-impact background ideas for your LinkedIn background banner image." }, style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                    }
                                }
                            }
                        }

                        item {
                            if (!viewModel.isGeneratingBrandKit) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("AI Executive Summary (Bio)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                            IconButton(onClick = {
                                                clipboardManager.setText(AnnotatedString(viewModel.linkedinBioProposal))
                                                Toast.makeText(context, "Bio copied!", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate100)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(viewModel.linkedinBioProposal.ifEmpty { "High-end corporate bio highlighting your background scenario." }, style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                    }
                                }
                            }
                        }

                        item {
                            if (!viewModel.isGeneratingBrandKit) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Professional Headlines / Slogans", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        if (viewModel.professionalSlogans.isEmpty()) {
                                            Text("Slogans will appear here.", color = Slate600)
                                        } else {
                                            viewModel.professionalSlogans.forEach { slogan ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("• $slogan", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                                    IconButton(onClick = {
                                                        clipboardManager.setText(AnnotatedString(slogan))
                                                        Toast.makeText(context, "Slogan copied!", Toast.LENGTH_SHORT).show()
                                                    }) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate100, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Career Advisor Chat
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Slate800, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.chatMessages) { msg ->
                                val isAI = msg.sender == "AI"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isAI) Slate900 else AccentPurple
                                        ),
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isAI) 4.dp else 16.dp,
                                            bottomEnd = if (isAI) 16.dp else 4.dp
                                        ),
                                        modifier = Modifier.widthIn(max = 240.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = if (isAI) "AI Coach" else "You",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isAI) AccentRose else Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(msg.text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                        }
                                    }
                                }
                            }
                            if (viewModel.isChatLoading) {
                                item {
                                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentRose, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Coach is drafting response...", color = Slate600, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    var chatInput by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Ask for executive positioning, networking strategy...") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Slate100,
                                focusedBorderColor = AccentPurple,
                                unfocusedBorderColor = Slate600,
                                focusedContainerColor = Slate800,
                                unfocusedContainerColor = Slate800
                            ),
                            maxLines = 2,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (chatInput.trim().isNotEmpty()) {
                                    viewModel.sendCareerMessage(chatInput)
                                    chatInput = ""
                                }
                            })
                        )
                        IconButton(
                            onClick = {
                                if (chatInput.trim().isNotEmpty()) {
                                    viewModel.sendCareerMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            modifier = Modifier
                                .background(AccentPurple, RoundedCornerShape(12.dp))
                                .size(48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }

            2 -> {
                // API Key Config & Settings
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate800),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Google Gemini API Key Config",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    "Overwrites default AI Studio workspace key. Keys are handled strictly on device.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = viewModel.customApiKey,
                                    onValueChange = { viewModel.updateCustomApiKey(it) },
                                    label = { Text("Gemini API Key") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Slate100,
                                        focusedBorderColor = AccentPurple,
                                        unfocusedBorderColor = Slate600,
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900
                                    ),
                                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                                            Icon(
                                                imageVector = if (apiKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle Key Visibility",
                                                tint = Slate100
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "If left empty, Persona Studio falls back to the injected server-side keys configured securely in your AI Studio Secrets panel.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Slate600
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate800),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("About Persona Studio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Version 1.0.0 Pro\nBuilt securely on Jetpack Compose and modern Kotlin REST APIs for the 2026 Android App Ecosystem.", style = MaterialTheme.typography.bodyMedium, color = Slate100)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Credits: Powered by Gemini & Imagen models.", style = MaterialTheme.typography.labelSmall, color = Slate600)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetFaceThumbnail(presetId: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                when (presetId) {
                    "sarah" -> Color(0xFF342F3D)       // Dark grey-purple studio background
                    "david" -> Color(0xFF1E293B)       // Corporate dark blue-grey
                    "elena" -> Color(0xFF4C1D95)       // Creative deep violet
                    "marcus" -> Color(0xFF064E3B)      // Tech emerald dark
                    else -> Slate900
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Common skin tone paint
            val skinColor = Color(0xFFFFD2A1)

            // Draw neck
            drawRect(
                color = skinColor,
                topLeft = Offset(w * 0.44f, h * 0.55f),
                size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.15f)
            )

            // Draw head
            drawCircle(
                color = skinColor,
                center = Offset(w * 0.5f, h * 0.42f),
                radius = w * 0.20f
            )

            // Draw eyes
            val eyeColor = if (presetId == "sarah") Color(0xFF78350F) else Color(0xFF1E293B)
            drawCircle(
                color = eyeColor,
                center = Offset(w * 0.43f, h * 0.42f),
                radius = w * 0.03f
            )
            drawCircle(
                color = eyeColor,
                center = Offset(w * 0.57f, h * 0.42f),
                radius = w * 0.03f
            )

            // Draw smile
            val mouthY = h * 0.50f
            drawLine(
                color = if (presetId == "sarah") Color(0xFFF43F5E) else Color(0xFFDC2626),
                start = Offset(w * 0.46f, mouthY),
                end = Offset(w * 0.54f, mouthY),
                strokeWidth = 3f
            )

            // Preset-specific details
            when (presetId) {
                "sarah" -> {
                    // Sleek tight bun on top of head
                    drawCircle(
                        color = Color(0xFF1C1917),
                        center = Offset(w * 0.5f, h * 0.19f),
                        radius = w * 0.07f
                    )
                    // Hair slicked back over head
                    drawCircle(
                        color = Color(0xFF1C1917),
                        center = Offset(w * 0.5f, h * 0.35f),
                        radius = w * 0.15f,
                        alpha = 0.5f
                    )
                    // Silver Earrings
                    drawCircle(
                        color = Color.White,
                        center = Offset(w * 0.28f, h * 0.45f),
                        radius = w * 0.03f
                    )
                    drawCircle(
                        color = Color.White,
                        center = Offset(w * 0.72f, h * 0.45f),
                        radius = w * 0.03f
                    )
                    // Orange-red sleeveless top
                    val redColor = Color(0xFFEA580C)
                    val shoulderPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.15f, h)
                        lineTo(w * 0.35f, h * 0.75f)
                        lineTo(w * 0.45f, h * 0.82f)
                        lineTo(w * 0.5f, h * 0.82f)
                        lineTo(w * 0.55f, h * 0.82f)
                        lineTo(w * 0.65f, h * 0.75f)
                        lineTo(w * 0.85f, h)
                        close()
                    }
                    drawPath(path = shoulderPath, color = redColor)
                }
                "david" -> {
                    // Grey-combed hair
                    val hairPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.3f, h * 0.42f)
                        quadraticTo(w * 0.32f, h * 0.25f, w * 0.5f, h * 0.22f)
                        quadraticTo(w * 0.68f, h * 0.25f, w * 0.7f, h * 0.42f)
                        lineTo(w * 0.3f, h * 0.42f)
                        close()
                    }
                    drawPath(path = hairPath, color = Color(0xFF9CA3AF))
                    // Corporate Suit
                    val suitPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.15f, h)
                        quadraticTo(w * 0.3f, h * 0.72f, w * 0.5f, h * 0.72f)
                        quadraticTo(w * 0.7f, h * 0.72f, w * 0.85f, h)
                        close()
                    }
                    drawPath(path = suitPath, color = Color(0xFF334155))
                    // White collar and red tie
                    val collarPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.44f, h * 0.72f)
                        lineTo(w * 0.50f, h * 0.80f)
                        lineTo(w * 0.56f, h * 0.72f)
                        close()
                    }
                    drawPath(path = collarPath, color = Color.White)
                    val tiePath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.48f, h * 0.78f)
                        lineTo(w * 0.52f, h * 0.78f)
                        lineTo(w * 0.53f, h * 0.95f)
                        lineTo(w * 0.50f, h * 0.98f)
                        lineTo(w * 0.47f, h * 0.95f)
                        close()
                    }
                    drawPath(path = tiePath, color = Color(0xFFEF4444))
                }
                "elena" -> {
                    // Creative purple/magenta aesthetic, stylish oversized glasses, designer outfit
                    drawCircle(
                        color = Color(0xFF1E1B4B), // Dark blue hair
                        center = Offset(w * 0.5f, h * 0.35f),
                        radius = w * 0.16f
                    )
                    // Glasses frames
                    drawCircle(
                        color = Color(0xFF0F172A),
                        center = Offset(w * 0.42f, h * 0.44f),
                        radius = w * 0.05f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                    drawCircle(
                        color = Color(0xFF0F172A),
                        center = Offset(w * 0.58f, h * 0.44f),
                        radius = w * 0.05f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                    // Glasses bridge
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = Offset(w * 0.47f, h * 0.44f),
                        end = Offset(w * 0.53f, h * 0.44f),
                        strokeWidth = 3f
                    )
                    // Creative modern outfit (teal/magenta)
                    val jacketPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.15f, h)
                        quadraticTo(w * 0.3f, h * 0.75f, w * 0.5f, h * 0.75f)
                        quadraticTo(w * 0.7f, h * 0.75f, w * 0.85f, h)
                        close()
                    }
                    drawPath(path = jacketPath, color = Color(0xFF0D9488))
                }
                "marcus" -> {
                    // Tech guy, wire glasses, minimalist dark shirt
                    val hairPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.32f, h * 0.42f)
                        quadraticTo(w * 0.35f, h * 0.28f, w * 0.5f, h * 0.26f)
                        quadraticTo(w * 0.65f, h * 0.28f, w * 0.68f, h * 0.42f)
                        close()
                    }
                    drawPath(path = hairPath, color = Color(0xFF111827))
                    // Round wire glasses
                    drawCircle(
                        color = Color(0xFF64748B),
                        center = Offset(w * 0.42f, h * 0.44f),
                        radius = w * 0.045f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                    drawCircle(
                        color = Color(0xFF64748B),
                        center = Offset(w * 0.58f, h * 0.44f),
                        radius = w * 0.045f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                    // Glasses bridge
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(w * 0.465f, h * 0.44f),
                        end = Offset(w * 0.535f, h * 0.44f),
                        strokeWidth = 2f
                    )
                    // Dark minimalist shirt
                    val shirtPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.15f, h)
                        quadraticTo(w * 0.3f, h * 0.75f, w * 0.5f, h * 0.75f)
                        quadraticTo(w * 0.7f, h * 0.75f, w * 0.85f, h)
                        close()
                    }
                    drawPath(path = shirtPath, color = Color(0xFF1E293B))
                }
                else -> {}
            }
        }
    }
}

fun saveImageToDownloads(context: Context, base64Str: String, fileNamePrefix: String): Boolean {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) ?: return false
        val filename = "${fileNamePrefix}_${System.currentTimeMillis()}.jpg"
        val contentResolver = context.contentResolver
        val imageDetails = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PersonaStudio")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails)
        if (imageUri != null) {
            contentResolver.openOutputStream(imageUri).use { outStream ->
                if (outStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.clear()
                imageDetails.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(imageUri, imageDetails, null, null)
            }
            true
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
