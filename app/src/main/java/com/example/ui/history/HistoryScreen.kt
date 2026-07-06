package com.example.ui.history

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoriteEntity
import com.example.data.HistoryEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val historyList by viewModel.historyList.collectAsState()
    val favoritesList by viewModel.favoritesList.collectAsState()

    var activeTab by remember { mutableStateOf("History") } // "History" or "Favorites"
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tab row: History vs Favorites
        TabRow(selectedTabIndex = if (activeTab == "History") 0 else 1) {
            Tab(
                selected = activeTab == "History",
                onClick = { activeTab = "History" },
                text = { Text("History") }
            )
            Tab(
                selected = activeTab == "Favorites",
                onClick = { activeTab = "Favorites" },
                text = { Text("Favorites") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search local data") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
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

        if (activeTab == "History") {
            // Header actions for History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Calculations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (historyList.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistory()
                            Toast.makeText(context, "History Cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val filteredHistory = historyList.filter {
                it.expression.contains(searchQuery, ignoreCase = true) ||
                        it.result.contains(searchQuery, ignoreCase = true)
            }

            if (filteredHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No calculations stored", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredHistory) { item ->
                        HistoryItemRow(item, viewModel, clipboard, context)
                    }
                }
            }
        } else {
            // Favorites view
            Text(
                "Saved Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val filteredFavorites = favoritesList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.targetId.contains(searchQuery, ignoreCase = true)
            }

            if (filteredFavorites.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No favorites saved yet", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredFavorites) { fav ->
                        FavoriteItemRow(fav, viewModel, clipboard, context)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    item: HistoryEntity,
    viewModel: MainViewModel,
    clipboard: androidx.compose.ui.platform.ClipboardManager,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPinned) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.expression,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    // Pin Button
                    IconButton(onClick = { viewModel.toggleHistoryPin(item) }) {
                        Icon(
                            imageVector = if (item.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = "Pin",
                            tint = if (item.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    // Delete Button
                    IconButton(onClick = { viewModel.deleteHistoryItem(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "= ${item.result}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        clipboard.setText(AnnotatedString(item.result))
                        Toast.makeText(context, "Result copied", Toast.LENGTH_SHORT).show()
                    }
                )

                Text(
                    text = item.category.uppercase(),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FavoriteItemRow(
    fav: FavoriteEntity,
    viewModel: MainViewModel,
    clipboard: androidx.compose.ui.platform.ClipboardManager,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("favorite_item_${fav.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(fav.type.uppercase(), fontSize = 10.sp) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fav.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fav.value.ifEmpty { fav.targetId },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Row {
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(fav.value.ifEmpty { fav.targetId }))
                        Toast.makeText(context, "Copied favorite detail", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                }

                IconButton(onClick = { viewModel.deleteFavoriteEntity(fav) }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
