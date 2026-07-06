package com.example.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val themeState by viewModel.theme.collectAsState()
    val accentState by viewModel.accentColor.collectAsState()
    val amoledState by viewModel.amoledMode.collectAsState()
    val precisionState by viewModel.decimalPrecision.collectAsState()
    val angleState by viewModel.angleMode.collectAsState()
    val hapticState by viewModel.hapticFeedback.collectAsState()
    val soundState by viewModel.soundFeedback.collectAsState()
    val largeTextState by viewModel.largeText.collectAsState()

    var activeSubPage by remember { mutableStateOf<String?>(null) } // null, "About", "Privacy", "Help"
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (activeSubPage != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(activeSubPage!!) },
                    navigationIcon = {
                        IconButton(onClick = { activeSubPage = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                when (activeSubPage) {
                    "About" -> AboutPageContent()
                    "Privacy" -> PrivacyPolicyContent()
                    "Help" -> HelpContent()
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Settings & Preferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Appearance Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Appearance", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Theme selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Theme")
                        var themeExpanded by remember { mutableStateOf(false) }
                        Box {
                            Button(onClick = { themeExpanded = true }) {
                                Text(themeState.replaceFirstChar { it.uppercase() })
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                                listOf("system", "light", "dark").forEach { mode ->
                                    DropdownMenuItem(
                                        text = { Text(mode.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            viewModel.updateTheme(mode)
                                            themeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // AMOLED Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("AMOLED Pitch-Black Mode")
                            Text("Saves battery on OLED screens", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = amoledState,
                            onCheckedChange = { viewModel.updateAmoledMode(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Accent Colors
                    Text("Accent Palette", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    val accents = listOf("blue", "emerald", "purple", "orange", "red", "cyan", "pink")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(accents) { accent ->
                            FilterChip(
                                selected = accentState == accent,
                                onClick = { viewModel.updateAccentColor(accent) },
                                label = { Text(accent.replaceFirstChar { it.uppercase() }, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Calculator configurations
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Calculator", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Decimal Precision Slider
                    Column {
                        Text("Decimal Precision: $precisionState")
                        Slider(
                            value = precisionState.toFloat(),
                            onValueChange = { viewModel.updateDecimalPrecision(it.toInt()) },
                            valueRange = 0f..10f,
                            steps = 9
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Angle Mode selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Default Angle Mode")
                        SingleChoiceSegmentedButtonRow {
                            val modes = listOf("degrees", "radians")
                            modes.forEachIndexed { idx, m ->
                                SegmentedButton(
                                    selected = angleState == m,
                                    onClick = { viewModel.updateAngleMode(m) },
                                    shape = SegmentedButtonDefaults.itemShape(index = idx, count = modes.size)
                                ) {
                                    Text(m.replaceFirstChar { it.uppercase() })
                                }
                            }
                        }
                    }
                }
            }
        }

        // Accessibility & System Preferences
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accessibility & System", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Large Text Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Large Font Numbers")
                        Switch(
                            checked = largeTextState,
                            onCheckedChange = { viewModel.updateLargeText(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Haptic Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Haptic Touch Feedback")
                        Switch(
                            checked = hapticState,
                            onCheckedChange = { viewModel.updateHapticFeedback(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sound Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Audio Click Feedback")
                        Switch(
                            checked = soundState,
                            onCheckedChange = { viewModel.updateSoundFeedback(it) }
                        )
                    }
                }
            }
        }

        // Secondary info links
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text("About CalcMax") },
                        leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier.clickable { activeSubPage = "About" }
                    )
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
                        modifier = Modifier.clickable { activeSubPage = "Privacy" }
                    )
                    ListItem(
                        headlineContent = { Text("Help & FAQs") },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = null) },
                        modifier = Modifier.clickable { activeSubPage = "Help" }
                    )
                    ListItem(
                        headlineContent = { Text("Rate Application") },
                        leadingContent = { Icon(Icons.Default.Star, contentDescription = null) },
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "This feature will be available after the official Play Store release.", Toast.LENGTH_LONG).show()
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Send Feedback Email") },
                        leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:zeroproductions010@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "CalcMax Feedback")
                                putExtra(Intent.EXTRA_TEXT, "Device: ${android.os.Build.MODEL}\nAndroid: ${android.os.Build.VERSION.RELEASE}\nApp version: 1.0.0")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email client configured", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Restore Default Settings") },
                        leadingContent = { Icon(Icons.Default.SettingsBackupRestore, contentDescription = null) },
                        modifier = Modifier.clickable {
                            viewModel.resetAllSettings()
                            Toast.makeText(context, "Settings Restored", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Copyright info footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CalcMax version 1.0.0 (Build 1)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text("© 2026 Zero. All rights reserved.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun AboutPageContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("CalcMax", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("All-in-One Calculator & Unit Converter", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Developer: Zero Productions", fontWeight = FontWeight.SemiBold)
            Text("Copyright: © 2026 Zero. All rights reserved.")
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "CalcMax is a powerful offline calculator designed for students, engineers, professionals and everyday users. It combines a scientific calculator, advanced graph plotting, dozens of unit converters and productivity tools into one fast and lightweight application.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Text("Key Features Included:", fontWeight = FontWeight.Bold)
            val features = listOf(
                "• Scientific Calculator with full memory and brackets",
                "• Interactive 2D Cartesian Function Plotting",
                "• Interactive 3D Wireframe Surface rendering",
                "• Sliding Variable transformations",
                "• Calculus Tangent line and Integral shading",
                "• Multi-category Offline Unit Converter with instant results",
                "• Saved favorites and searchable History logs",
                "• Beautiful Material You colors and AMOLED Dark mode"
            )
            features.forEach { feat ->
                Text(feat, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Privacy Policy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("CalcMax respects your privacy.", fontWeight = FontWeight.SemiBold)
        }

        item {
            val terms = listOf(
                "• This application works entirely offline.",
                "• No internet connection is required or requested.",
                "• No personal information is collected, stored, or processed.",
                "• No advertisements are shown.",
                "• No analytics, telemetry, or tracking services are integrated.",
                "• No background data is transmitted.",
                "• All history and favorites data remain stored locally on your own device.",
                "• You can clear your entire calculation history at any time.",
                "• No information is shared with third parties."
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    terms.forEach { term ->
                        Text(term, fontSize = 14.sp, lineHeight = 22.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HelpContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Help & Frequently Asked Questions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            HelpSection(
                title = "Standard & Scientific Calculator",
                body = "Use standard keys for basic math. Scientific functions require matching parentheses. For example, to calculate sine, write: sin(pi / 4). Use memory buttons (MC, MR, M+, M-) to manage active values."
            )
        }

        item {
            HelpSection(
                title = "Graphing Functions",
                body = "In the 2D graphing tab, type your equation in terms of 'x' (e.g. sin(x)). You can also use parameters a, b, or c and slide their values below the canvas to see immediate transformations! Toggle 'Tangent' to drag a slope tangent line across coordinates."
            )
        }

        item {
            HelpSection(
                title = "3D Graphing",
                body = "Toggle the segmented button to '3D'. Enter functions in terms of 'x' and 'y' (e.g. sin(x) * cos(y)). Drag your finger across the viewport to rotate the wireframe surface dynamically in 3D projection space!"
            )
        }

        item {
            HelpSection(
                title = "Offline Conversions",
                body = "Open the Converters tab. Search for categories or click on card icons. Type input values inside the 'From' field to see instant results in the 'To' field. Swap conversion directions with the center arrows."
            )
        }
    }
}

@Composable
fun HelpSection(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(body, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
