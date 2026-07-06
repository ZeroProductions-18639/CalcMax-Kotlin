package com.example.ui.calculator

import android.widget.Toast
import com.example.domain.SpecializedCalculators
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.luminance
import com.example.ui.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorScreen(viewModel: MainViewModel) {
    val inputExpr by viewModel.calcInputExpression.collectAsState()
    val outputRes by viewModel.calcOutputResult.collectAsState()
    val activeMode by viewModel.calculatorMode.collectAsState()
    val angleMode by viewModel.angleMode.collectAsState()
    val largeText by viewModel.largeText.collectAsState()
    val hapticFeedbackEnabled by viewModel.hapticFeedback.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val programmerBases by viewModel.programmerBases.collectAsState()
    val currentProgBase by viewModel.programmerBase.collectAsState()

    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    fun triggerHaptic() {
        if (hapticFeedbackEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Toggle calculator sub-mode (Standard / Scientific / Programmer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SingleChoiceSegmentedButtonRow {
                val modes = listOf("standard", "scientific", "programmer")
                modes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = activeMode == mode,
                        onClick = {
                            triggerHaptic()
                            viewModel.calculatorMode.value = mode
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
                    ) {
                        Text(mode.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display panel with expression and result
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF14171E) else Color(0xFFF8F9FA)
            ),
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (activeMode == "programmer") {
                    // Programmer Multi-Base Live Display Panel
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("HEX", "DEC", "OCT", "BIN").forEach { base ->
                            val isSelected = currentProgBase == base
                            val value = programmerBases[base] ?: "0"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) {
                                            when (accentColor) {
                                                "emerald" -> Color(0xFF00C853).copy(alpha = 0.15f)
                                                "purple" -> Color(0xFF7F3DEC).copy(alpha = 0.15f)
                                                "orange" -> Color(0xFFC44300).copy(alpha = 0.15f)
                                                "red" -> Color(0xFFC01234).copy(alpha = 0.15f)
                                                "cyan" -> Color(0xFF006A7A).copy(alpha = 0.15f)
                                                "pink" -> Color(0xFFBA0063).copy(alpha = 0.15f)
                                                else -> Color(0xFF0F5CD8).copy(alpha = 0.15f)
                                            }
                                        } else Color.Transparent
                                    )
                                    .combinedClickable(
                                        onClick = {
                                            triggerHaptic()
                                            viewModel.programmerBase.value = base
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = base,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        when (accentColor) {
                                            "emerald" -> if (isDark) Color(0xFF62DC9F) else Color(0xFF00875A)
                                            "purple" -> if (isDark) Color(0xFFD6BAFF) else Color(0xFF7F3DEC)
                                            "orange" -> if (isDark) Color(0xFFFFB596) else Color(0xFFC44300)
                                            "red" -> if (isDark) Color(0xFFFFB3B8) else Color(0xFFC01234)
                                            "cyan" -> if (isDark) Color(0xFF80D4E6) else Color(0xFF006A7A)
                                            "pink" -> if (isDark) Color(0xFFFFB1C8) else Color(0xFFBA0063)
                                            else -> if (isDark) Color(0xFFADC6FF) else Color(0xFF0F5CD8)
                                        }
                                    } else if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = value,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDark) Color.White else Color.Black,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Divider
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                            .padding(vertical = 4.dp)
                    )

                    // Input Expression Row
                    Text(
                        text = inputExpr.ifEmpty { "0" },
                        fontSize = if (largeText) 28.sp else 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calc_expression"),
                        lineHeight = 32.sp,
                        maxLines = 2
                    )
                } else {
                    // Standard & Scientific Mode Display
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        // Expression Row
                        Text(
                            text = inputExpr.ifEmpty { "0" },
                            fontSize = if (largeText) 36.sp else 28.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calc_expression"),
                            lineHeight = 38.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Output Result Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (activeMode == "scientific") {
                                AssistChip(
                                    onClick = {
                                        triggerHaptic()
                                        viewModel.updateAngleMode(if (angleMode == "degrees") "radians" else "degrees")
                                    },
                                    label = { Text(angleMode.uppercase()) }
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            Text(
                                text = outputRes,
                                fontSize = if (largeText) 48.sp else 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (accentColor) {
                                    "emerald" -> if (isDark) Color(0xFF62DC9F) else Color(0xFF00875A)
                                    "purple" -> if (isDark) Color(0xFFD6BAFF) else Color(0xFF7F3DEC)
                                    "orange" -> if (isDark) Color(0xFFFFB596) else Color(0xFFC44300)
                                    "red" -> if (isDark) Color(0xFFFFB3B8) else Color(0xFFC01234)
                                    "cyan" -> if (isDark) Color(0xFF80D4E6) else Color(0xFF006A7A)
                                    "pink" -> if (isDark) Color(0xFFFFB1C8) else Color(0xFFBA0063)
                                    else -> if (isDark) Color(0xFFADC6FF) else Color(0xFF0F5CD8)
                                },
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .testTag("calc_result")
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            triggerHaptic()
                                            if (outputRes.isNotEmpty()) {
                                                clipboard.setText(AnnotatedString(outputRes))
                                                Toast.makeText(context, "Copied result to clipboard", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid of Keys
        val keys = when (activeMode) {
            "standard" -> listOf(
                "C", "(", ")", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "⌫", "="
            )
            "scientific" -> listOf(
                "MC", "MR", "M+", "M-",
                "sin", "cos", "tan", "^",
                "ln", "log", "√", "!",
                "π", "e", "(", ")",
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "-",
                "C", "0", ".", "="
            )
            else -> listOf( // Programmer Mode
                "HEX", "DEC", "OCT", "BIN",
                "AND", "OR", "XOR", "NOT",
                "A", "B", "C", "D",
                "E", "F", "LSH", "RSH",
                "7", "8", "9", "⌫",
                "4", "5", "6", "C",
                "1", "2", "3", "=",
                "0"
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(keys) { key ->
                // Enable or disable keys in Programmer Mode based on selected input base
                val isEnabled = when (activeMode) {
                    "programmer" -> {
                        when (key) {
                            "A", "B", "C", "D", "E", "F" -> currentProgBase == "HEX"
                            "8", "9" -> currentProgBase == "HEX" || currentProgBase == "DEC"
                            "2", "3", "4", "5", "6", "7" -> currentProgBase == "HEX" || currentProgBase == "DEC" || currentProgBase == "OCT"
                            "0", "1" -> true
                            "HEX", "DEC", "OCT", "BIN", "AND", "OR", "XOR", "NOT", "LSH", "RSH", "C", "⌫", "=" -> true
                            else -> false // disable "." or other keys in programmer mode
                        }
                    }
                    else -> true
                }

                CalculatorButton(
                    label = key,
                    activeMode = activeMode,
                    accentColor = accentColor,
                    isDark = isDark,
                    enabled = isEnabled,
                    onClick = {
                        triggerHaptic()
                        handleKeyAction(key, viewModel, context, clipboard)
                    }
                )
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    activeMode: String,
    accentColor: String,
    isDark: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isOperator = label in listOf("+", "-", "×", "÷", "=", "AND", "OR", "XOR", "NOT", "LSH", "RSH")
    val isControl = label in listOf("C", "⌫", "MC", "MR", "M+", "M-")
    val isEquals = label == "="

    // Dynamic coloring matching active theme accent and layout constraints
    val containerColor = when {
        !enabled -> if (isDark) Color(0xFF161A22).copy(alpha = 0.4f) else Color(0xFFECEFF1).copy(alpha = 0.4f)
        isEquals -> {
            when (accentColor) {
                "emerald" -> if (isDark) Color(0xFF00C853) else Color(0xFF00875A)
                "purple" -> if (isDark) Color(0xFFB388FF) else Color(0xFF7F3DEC)
                "orange" -> if (isDark) Color(0xFFFFAB40) else Color(0xFFC44300)
                "red" -> if (isDark) Color(0xFFFF5252) else Color(0xFFC01234)
                "cyan" -> if (isDark) Color(0xFF00E5FF) else Color(0xFF006A7A)
                "pink" -> if (isDark) Color(0xFFFF4081) else Color(0xFFBA0063)
                else -> if (isDark) Color(0xFF448AFF) else Color(0xFF0F5CD8)
            }
        }
        isOperator -> {
            if (isDark) {
                when (accentColor) {
                    "emerald" -> Color(0xFF0C2A1E)
                    "purple" -> Color(0xFF231B32)
                    "orange" -> Color(0xFF2E1911)
                    "red" -> Color(0xFF2E1314)
                    "cyan" -> Color(0xFF0A252D)
                    "pink" -> Color(0xFF2E101D)
                    else -> Color(0xFF111E32)
                }
            } else {
                when (accentColor) {
                    "emerald" -> Color(0xFFE8F5E9)
                    "purple" -> Color(0xFFF3E5F5)
                    "orange" -> Color(0xFFFFF3E0)
                    "red" -> Color(0xFFFFEBEE)
                    "cyan" -> Color(0xFFE0F7FA)
                    "pink" -> Color(0xFFFCE4EC)
                    else -> Color(0xFFE3F2FD)
                }
            }
        }
        isControl -> {
            if (label in listOf("C", "⌫")) {
                if (isDark) Color(0xFF3F1919) else Color(0xFFFFEBEE)
            } else {
                if (isDark) Color(0xFF202730) else Color(0xFFECEFF1)
            }
        }
        else -> {
            // Numbers and normal functions - Elegant card look with solid visual edges
            if (isDark) Color(0xFF1E222B) else Color(0xFFF1F3F4)
        }
    }

    val contentColor = when {
        !enabled -> if (isDark) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.25f)
        isEquals -> Color.White
        isOperator -> {
            when (accentColor) {
                "emerald" -> if (isDark) Color(0xFF62DC9F) else Color(0xFF00875A)
                "purple" -> if (isDark) Color(0xFFD6BAFF) else Color(0xFF7F3DEC)
                "orange" -> if (isDark) Color(0xFFFFB596) else Color(0xFFC44300)
                "red" -> if (isDark) Color(0xFFFFB3B8) else Color(0xFFC01234)
                "cyan" -> if (isDark) Color(0xFF80D4E6) else Color(0xFF006A7A)
                "pink" -> if (isDark) Color(0xFFFFB1C8) else Color(0xFFBA0063)
                else -> if (isDark) Color(0xFFADC6FF) else Color(0xFF0F5CD8)
            }
        }
        isControl -> {
            if (label in listOf("C", "⌫")) {
                if (isDark) Color(0xFFFF8A80) else Color(0xFFC62828)
            } else {
                if (isDark) Color(0xFFB0BEC5) else Color(0xFF37474F)
            }
        }
        else -> {
            if (isDark) Color.White else Color(0xFF202124)
        }
    }

    // Border stroke so every button has beautifully visible corners, edges, and borders!
    val borderStroke = if (!enabled) {
        BorderStroke(1.dp, Color.Transparent)
    } else if (isDark) {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    } else {
        BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
    }

    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(
                when (activeMode) {
                    "standard" -> 58.dp
                    "scientific" -> 44.dp
                    else -> 44.dp
                }
            )
            .fillMaxWidth()
            .testTag("btn_$label"),
        shape = RoundedCornerShape(12.dp), // Distinct elegant rounded corners!
        border = borderStroke,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        contentPadding = PaddingValues(2.dp)
    ) {
        if (label == "⌫") {
            Icon(
                Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = contentColor
            )
        } else {
            Text(
                text = label,
                fontSize = if (label.length > 3) 11.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun handleKeyAction(
    key: String,
    viewModel: MainViewModel,
    context: android.content.Context,
    clipboard: androidx.compose.ui.platform.ClipboardManager
) {
    when (key) {
        "C" -> viewModel.onCalculatorClear()
        "⌫" -> viewModel.onCalculatorBackspace()
        "=" -> viewModel.evaluateCalculatorExpression()
        "π" -> viewModel.onCalculatorInput("π")
        "e" -> viewModel.onCalculatorInput("e")
        "sin", "cos", "tan", "ln", "log", "√" -> viewModel.onCalculatorInput("$key(")
        "MC" -> {
            viewModel.calcMemory.value = 0.0
            Toast.makeText(context, "Memory Cleared", Toast.LENGTH_SHORT).show()
        }
        "MR" -> {
            viewModel.onCalculatorInput(SpecializedCalculators.formatVal(viewModel.calcMemory.value))
        }
        "M+" -> {
            val currentVal = viewModel.calcOutputResult.value.toDoubleOrNull() ?: 0.0
            viewModel.calcMemory.value += currentVal
            Toast.makeText(context, "Added to Memory", Toast.LENGTH_SHORT).show()
        }
        "M-" -> {
            val currentVal = viewModel.calcOutputResult.value.toDoubleOrNull() ?: 0.0
            viewModel.calcMemory.value -= currentVal
            Toast.makeText(context, "Subtracted from Memory", Toast.LENGTH_SHORT).show()
        }
        "MS" -> {
            val currentVal = viewModel.calcOutputResult.value.toDoubleOrNull() ?: 0.0
            viewModel.calcMemory.value = currentVal
            Toast.makeText(context, "Saved to Memory", Toast.LENGTH_SHORT).show()
        }
        "HEX", "DEC", "OCT", "BIN" -> {
            viewModel.programmerBase.value = key
            Toast.makeText(context, "Input base set to $key", Toast.LENGTH_SHORT).show()
        }
        else -> viewModel.onCalculatorInput(key)
    }
}
