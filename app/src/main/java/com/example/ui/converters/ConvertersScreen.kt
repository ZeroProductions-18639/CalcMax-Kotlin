package com.example.ui.converters

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ConverterCategory
import com.example.domain.ConverterUnit
import com.example.domain.UnitConverter
import com.example.ui.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertersScreen(viewModel: MainViewModel) {
    val activeCategoryName by viewModel.activeConverterCategory.collectAsState()
    val recentCategories by viewModel.recentConverters.collectAsState()
    val favoritesList by viewModel.favoritesList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (activeCategoryName == null) {
        // Categories Selection Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Converters") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("converter_search_input"),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recently Used section
            if (recentCategories.isNotEmpty() && searchQuery.isEmpty()) {
                Text(
                    text = "Recently Used",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentCategories.take(4)) { catName ->
                        val cat = UnitConverter.categories.find { it.name == catName }
                        if (cat != null) {
                            ConverterCategoryCard(cat) {
                                viewModel.activeConverterCategory.value = cat.name
                                viewModel.addRecentConverter(cat.name)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "All Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Category Card Grid
            val filteredCategories = UnitConverter.categories.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(filteredCategories) { cat ->
                    ConverterCategoryCard(cat) {
                        viewModel.activeConverterCategory.value = cat.name
                        viewModel.addRecentConverter(cat.name)
                    }
                }
            }
        }
    } else {
        // Active Unit Conversion screen
        val cat = UnitConverter.categories.find { it.name == activeCategoryName }
        if (cat != null) {
            ActiveConverterScreen(cat, viewModel) {
                viewModel.activeConverterCategory.value = null
            }
        }
    }
}

@Composable
fun ConverterCategoryCard(category: ConverterCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("category_card_${category.name}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (category.name) {
                    "Length" -> Icons.Default.Straighten
                    "Area" -> Icons.Default.AspectRatio
                    "Volume" -> Icons.Default.LocalDrink
                    "Mass" -> Icons.Default.MonitorWeight
                    "Time" -> Icons.Default.AccessTime
                    "Speed" -> Icons.Default.Speed
                    "Temperature" -> Icons.Default.DeviceThermostat
                    "Pressure" -> Icons.Default.Compress
                    "Force" -> Icons.Default.FitnessCenter
                    "Power" -> Icons.Default.ElectricBolt
                    "Angle" -> Icons.Default.RotateRight
                    "Data Storage" -> Icons.Default.SdCard
                    "Number System" -> Icons.Default.Filter1
                    else -> Icons.Default.Category
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveConverterScreen(
    category: ConverterCategory,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var inputVal by remember { mutableStateOf("1") }
    var selectedFromUnit by remember { mutableStateOf(category.units.firstOrNull() ?: ConverterUnit("", "", 1.0)) }
    var selectedToUnit by remember { mutableStateOf(category.units.getOrNull(1) ?: category.units.firstOrNull() ?: ConverterUnit("", "", 1.0)) }

    val outputVal = UnitConverter.convert(inputVal, selectedFromUnit, selectedToUnit, category)

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val isFavFlow = viewModel.isFavorite("converter", category.name).collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(category.name, "converter", category.name, "${selectedFromUnit.symbol} to ${selectedToUnit.symbol}")
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavFlow.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavFlow.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "From" Input card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("From", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom dropdown for From unit
                        Box {
                            Button(onClick = { fromExpanded = true }) {
                                Text(selectedFromUnit.name)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                                category.units.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            selectedFromUnit = unit
                                            fromExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Input Box
                        OutlinedTextField(
                            value = inputVal,
                            onValueChange = { inputVal = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                                .testTag("converter_input_value"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Swap units button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val temp = selectedFromUnit
                        selectedFromUnit = selectedToUnit
                        selectedToUnit = temp
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Units",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // "To" Result card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("To", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom dropdown for To unit
                        Box {
                            Button(onClick = { toExpanded = true }) {
                                Text(selectedToUnit.name)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                                category.units.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            selectedToUnit = unit
                                            toExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Output Display Text
                        Text(
                            text = outputVal,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                                .testTag("converter_output_value")
                                .clickable {
                                    if (outputVal.isNotEmpty() && outputVal != "Error") {
                                        clipboard.setText(AnnotatedString(outputVal))
                                        Toast.makeText(context, "Copied result", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        )
                    }
                }
            }

            // Quick Operations Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        inputVal = ""
                    }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }

                Button(
                    onClick = {
                        if (outputVal.isNotEmpty() && outputVal != "Error") {
                            clipboard.setText(AnnotatedString(outputVal))
                            Toast.makeText(context, "Copied conversion", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(Icons.Default.CopyAll, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
            }
        }
    }
}
