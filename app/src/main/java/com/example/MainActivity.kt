package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.converters.ConvertersScreen
import com.example.ui.graph.GraphingScreen
import com.example.ui.history.HistoryScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instantiate main ViewModel using the factory
        val viewModel: MainViewModel by viewModels {
            MainViewModelFactory(application)
        }

        setContent {
            // Observe settings reactivity
            val themeState by viewModel.theme.collectAsState()
            val accentState by viewModel.accentColor.collectAsState()
            val amoledState by viewModel.amoledMode.collectAsState()

            val darkTheme = when (themeState) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MyApplicationTheme(
                darkTheme = darkTheme,
                accentColor = accentState,
                amoledMode = amoledState,
                dynamicColor = false // Force custom color palettes
            ) {
                MainLayout(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: MainViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (activeTab) {
                            "calculator" -> "CalcMax Calculator"
                            "converters" -> "Unit Converters"
                            "graph" -> "Graphing Utilities"
                            "history" -> "History & Saved"
                            "settings" -> "Preferences"
                            else -> "CalcMax"
                        }
                    )
                },
                actions = {
                    // Quick Settings Shortcut
                    IconButton(onClick = { viewModel.activeTab.value = "settings" }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    // Overflow Menu Trigger
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Preferences") },
                            onClick = {
                                viewModel.activeTab.value = "settings"
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Share Application") },
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "I'm using CalcMax, an offline all-in-one calculator with scientific functions, graph plotting and dozens of unit converters.")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Send Feedback") },
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:zeroproductions010@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "CalcMax Feedback")
                                    putExtra(Intent.EXTRA_TEXT, "Device: ${android.os.Build.MODEL}\nAndroid: ${android.os.Build.VERSION.RELEASE}")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No email client configured", Toast.LENGTH_SHORT).show()
                                }
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("About CalcMax") },
                            onClick = {
                                viewModel.activeTab.value = "settings"
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = activeTab == "calculator",
                    onClick = { viewModel.activeTab.value = "calculator" },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                    label = { Text("Calculator") },
                    modifier = Modifier.testTag("tab_calculator")
                )
                NavigationBarItem(
                    selected = activeTab == "converters",
                    onClick = { viewModel.activeTab.value = "converters" },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Converters") },
                    label = { Text("Converters") },
                    modifier = Modifier.testTag("tab_converters")
                )
                NavigationBarItem(
                    selected = activeTab == "graph",
                    onClick = { viewModel.activeTab.value = "graph" },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Graph") },
                    label = { Text("Graph") },
                    modifier = Modifier.testTag("tab_graph")
                )
                NavigationBarItem(
                    selected = activeTab == "history",
                    onClick = { viewModel.activeTab.value = "history" },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    modifier = Modifier.testTag("tab_history")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "calculator" -> CalculatorScreen(viewModel)
                "converters" -> ConvertersScreen(viewModel)
                "graph" -> GraphingScreen(viewModel)
                "history" -> HistoryScreen(viewModel)
                "settings" -> SettingsScreen(viewModel)
            }
        }
    }
}
