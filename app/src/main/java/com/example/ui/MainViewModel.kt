package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val historyDao = db.historyDao()
    private val graphDao = db.graphDao()
    private val favoriteDao = db.favoriteDao()
    private val settings = SettingsDataStore(application)

    // App Navigation & Session States
    val activeTab = MutableStateFlow("calculator") // "calculator", "converters", "graph", "history"
    val calculatorMode = MutableStateFlow("scientific") // "standard", "scientific", "programmer"
    val activeCalculatorType = MutableStateFlow<String?>(null) // e.g. finance_emi, health_bmi if a sub-calculator is open
    val activeConverterCategory = MutableStateFlow<String?>(null) // if a specific converter is open

    // Preferences
    val theme = settings.themeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val accentColor = settings.accentColorFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "blue")
    val amoledMode = settings.amoledModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val decimalPrecision = settings.decimalPrecisionFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)
    val scientificNotation = settings.scientificNotationFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val thousandsSeparator = settings.thousandsSeparatorFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val angleMode = settings.angleModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "degrees")
    
    val historyLimit = settings.historyLimitFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)
    val graphAnimationSpeed = settings.graphAnimationSpeedFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)
    val graphQuality = settings.graphQualityFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "high")
    val graphGrid = settings.graphGridFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val graphAxisLabels = settings.graphAxisLabelsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val largeText = settings.largeTextFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val hapticFeedback = settings.hapticFeedbackFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val soundFeedback = settings.soundFeedbackFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val recentConverters = settings.recentConvertersFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentCalculators = settings.recentCalculatorsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database Flows
    val historyList = historyDao.getAllHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savedGraphs = graphDao.getAllGraphs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val favoritesList = favoriteDao.getAllFavorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query state
    val globalSearchQuery = MutableStateFlow("")
    
    // Calculator States
    val calcInputExpression = MutableStateFlow("")
    val calcOutputResult = MutableStateFlow("")
    val calcMemory = MutableStateFlow(0.0)
    val calcHistoryStack = MutableStateFlow<List<String>>(emptyList())
    val calcRedoStack = MutableStateFlow<List<String>>(emptyList())
    val programmerBase = MutableStateFlow("DEC") // "HEX", "DEC", "OCT", "BIN"
    
    // Real-time bases map for Programmer Mode
    val programmerBases = combine(calcInputExpression, programmerBase, calculatorMode) { expr, base, mode ->
        if (mode != "programmer") return@combine emptyMap<String, String>()
        if (expr.isBlank()) {
            return@combine mapOf("HEX" to "0", "DEC" to "0", "OCT" to "0", "BIN" to "0")
        }
        try {
            val preprocessed = ExpressionParser.preprocessProgrammerExpression(expr, base)
            val result = ExpressionParser.evaluate(preprocessed, emptyMap(), false)
            if (result.isNaN() || result.isInfinite()) {
                mapOf("HEX" to "Error", "DEC" to "Error", "OCT" to "Error", "BIN" to "Error")
            } else {
                val value = result.toLong()
                mapOf(
                    "HEX" to value.toString(16).uppercase(),
                    "DEC" to value.toString(10),
                    "OCT" to value.toString(8),
                    "BIN" to value.toString(2)
                )
            }
        } catch (e: Exception) {
            mapOf("HEX" to "Error", "DEC" to "Error", "OCT" to "Error", "BIN" to "Error")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mapOf("HEX" to "0", "DEC" to "0", "OCT" to "0", "BIN" to "0"))

    fun updateTheme(value: String) = viewModelScope.launch { settings.updateTheme(value) }
    fun updateAccentColor(value: String) = viewModelScope.launch { settings.updateAccentColor(value) }
    fun updateAmoledMode(value: Boolean) = viewModelScope.launch { settings.updateAmoledMode(value) }
    fun updateDecimalPrecision(value: Int) = viewModelScope.launch { settings.updateDecimalPrecision(value) }
    fun updateScientificNotation(value: Boolean) = viewModelScope.launch { settings.updateScientificNotation(value) }
    fun updateThousandsSeparator(value: Boolean) = viewModelScope.launch { settings.updateThousandsSeparator(value) }
    fun updateAngleMode(value: String) = viewModelScope.launch { settings.updateAngleMode(value) }
    
    fun updateHistoryLimit(value: Int) = viewModelScope.launch { settings.updateHistoryLimit(value) }
    fun updateGraphAnimationSpeed(value: Float) = viewModelScope.launch { settings.updateGraphAnimationSpeed(value) }
    fun updateGraphQuality(value: String) = viewModelScope.launch { settings.updateGraphQuality(value) }
    fun updateGraphGrid(value: Boolean) = viewModelScope.launch { settings.updateGraphGrid(value) }
    fun updateGraphAxisLabels(value: Boolean) = viewModelScope.launch { settings.updateGraphAxisLabels(value) }
    
    fun updateLargeText(value: Boolean) = viewModelScope.launch { settings.updateLargeText(value) }
    fun updateHapticFeedback(value: Boolean) = viewModelScope.launch { settings.updateHapticFeedback(value) }
    fun updateSoundFeedback(value: Boolean) = viewModelScope.launch { settings.updateSoundFeedback(value) }

    fun addRecentConverter(category: String) = viewModelScope.launch { settings.addRecentConverter(category) }
    fun addRecentCalculator(calcId: String) = viewModelScope.launch { settings.addRecentCalculator(calcId) }
    fun resetAllSettings() = viewModelScope.launch { settings.resetAll() }

    // History Database operations
    fun addHistoryItem(expression: String, result: String, category: String = "calculator") = viewModelScope.launch {
        historyDao.insertHistory(HistoryEntity(expression = expression, result = result, category = category))
    }

    fun deleteHistoryItem(entity: HistoryEntity) = viewModelScope.launch {
        historyDao.deleteHistory(entity)
    }

    fun clearAllHistory() = viewModelScope.launch {
        historyDao.clearAllHistory()
    }

    fun toggleHistoryFavorite(entity: HistoryEntity) = viewModelScope.launch {
        historyDao.updateHistory(entity.copy(isFavorite = !entity.isFavorite))
    }

    fun toggleHistoryPin(entity: HistoryEntity) = viewModelScope.launch {
        historyDao.updateHistory(entity.copy(isPinned = !entity.isPinned))
    }

    // Graph Database operations
    fun saveGraph(name: String, expression: String, color: String = "#2196F3") = viewModelScope.launch {
        graphDao.insertGraph(GraphEntity(name = name, expression = expression, colorHex = color))
    }

    fun deleteGraph(entity: GraphEntity) = viewModelScope.launch {
        graphDao.deleteGraph(entity)
    }

    fun updateGraph(entity: GraphEntity) = viewModelScope.launch {
        graphDao.updateGraph(entity)
    }

    // Favorites operations
    fun toggleFavorite(title: String, type: String, targetId: String, value: String = "") = viewModelScope.launch {
        val exists = favoriteDao.isFavorite(type, targetId).first()
        if (exists) {
            favoriteDao.deleteFavorite(type, targetId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(title = title, type = type, targetId = targetId, value = value))
        }
    }

    fun isFavorite(type: String, targetId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(type, targetId)
    }

    fun deleteFavoriteEntity(entity: FavoriteEntity) = viewModelScope.launch {
        favoriteDao.deleteFavoriteEntity(entity)
    }

    // Calculator state updates
    fun onCalculatorInput(char: String) {
        val current = calcInputExpression.value
        calcHistoryStack.value = calcHistoryStack.value + current
        calcRedoStack.value = emptyList() // clear redo on new input
        calcInputExpression.value = current + char
    }

    fun onCalculatorBackspace() {
        val current = calcInputExpression.value
        if (current.isNotEmpty()) {
            calcHistoryStack.value = calcHistoryStack.value + current
            calcRedoStack.value = emptyList()
            calcInputExpression.value = current.substring(0, current.length - 1)
        }
    }

    fun onCalculatorClear() {
        calcHistoryStack.value = calcHistoryStack.value + calcInputExpression.value
        calcRedoStack.value = emptyList()
        calcInputExpression.value = ""
        calcOutputResult.value = ""
    }

    fun onCalculatorUndo() {
        val history = calcHistoryStack.value
        if (history.isNotEmpty()) {
            val prev = history.last()
            calcRedoStack.value = calcRedoStack.value + calcInputExpression.value
            calcInputExpression.value = prev
            calcHistoryStack.value = history.dropLast(1)
        }
    }

    fun onCalculatorRedo() {
        val redo = calcRedoStack.value
        if (redo.isNotEmpty()) {
            val next = redo.last()
            calcHistoryStack.value = calcHistoryStack.value + calcInputExpression.value
            calcInputExpression.value = next
            calcRedoStack.value = redo.dropLast(1)
        }
    }

    fun evaluateCalculatorExpression() {
        val expr = calcInputExpression.value
        if (expr.isBlank()) return
        viewModelScope.launch {
            if (calculatorMode.value == "programmer") {
                val base = programmerBase.value
                val preprocessed = ExpressionParser.preprocessProgrammerExpression(expr, base)
                val result = ExpressionParser.evaluate(preprocessed, emptyMap(), false)
                if (result.isNaN() || result.isInfinite()) {
                    calcOutputResult.value = "Error"
                } else {
                    val longVal = result.toLong()
                    val formatted = when (base) {
                        "HEX" -> longVal.toString(16).uppercase()
                        "OCT" -> longVal.toString(8)
                        "BIN" -> longVal.toString(2)
                        else -> longVal.toString(10)
                    }
                    calcOutputResult.value = formatted
                    addHistoryItem(expr, formatted, "programmer")
                }
            } else {
                val variables = mutableMapOf<String, Double>()
                val useDegrees = angleMode.value == "degrees"
                val result = ExpressionParser.evaluate(expr, variables, useDegrees)
                if (result.isNaN()) {
                    calcOutputResult.value = "Error"
                } else {
                    val formatted = SpecializedCalculators.formatVal(result, decimalPrecision.value)
                    calcOutputResult.value = formatted
                    addHistoryItem(expr, formatted, "calculator")
                }
            }
        }
    }
}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
