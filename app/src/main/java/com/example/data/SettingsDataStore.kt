package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calcmax_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val THEME = stringPreferencesKey("theme") // "system", "light", "dark"
        val ACCENT_COLOR = stringPreferencesKey("accent_color") // "blue", "emerald", "purple", "orange", "red", "cyan", "pink", "material"
        val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
        val DECIMAL_PRECISION = intPreferencesKey("decimal_precision")
        val SCIENTIFIC_NOTATION = booleanPreferencesKey("scientific_notation")
        val THOUSANDS_SEPARATOR = booleanPreferencesKey("thousands_separator")
        val ANGLE_MODE = stringPreferencesKey("angle_mode") // "degrees", "radians", "gradians"
        
        val HISTORY_LIMIT = intPreferencesKey("history_limit") // -1 for unlimited
        val GRAPH_ANIMATION_SPEED = floatPreferencesKey("graph_animation_speed")
        val GRAPH_QUALITY = stringPreferencesKey("graph_quality") // "low", "medium", "high"
        val GRAPH_GRID = booleanPreferencesKey("graph_grid")
        val GRAPH_AXIS_LABELS = booleanPreferencesKey("graph_axis_labels")
        
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_FEEDBACK = booleanPreferencesKey("sound_feedback")
        
        val RECENT_CONVERTERS = stringPreferencesKey("recent_converters") // Comma-separated list
        val RECENT_CALCULATORS = stringPreferencesKey("recent_calculators") // Comma-separated list
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { it[THEME] ?: "system" }
    val accentColorFlow: Flow<String> = context.dataStore.data.map { it[ACCENT_COLOR] ?: "blue" }
    val amoledModeFlow: Flow<Boolean> = context.dataStore.data.map { it[AMOLED_MODE] ?: false }
    val decimalPrecisionFlow: Flow<Int> = context.dataStore.data.map { it[DECIMAL_PRECISION] ?: 6 }
    val scientificNotationFlow: Flow<Boolean> = context.dataStore.data.map { it[SCIENTIFIC_NOTATION] ?: false }
    val thousandsSeparatorFlow: Flow<Boolean> = context.dataStore.data.map { it[THOUSANDS_SEPARATOR] ?: true }
    val angleModeFlow: Flow<String> = context.dataStore.data.map { it[ANGLE_MODE] ?: "degrees" }
    
    val historyLimitFlow: Flow<Int> = context.dataStore.data.map { it[HISTORY_LIMIT] ?: -1 }
    val graphAnimationSpeedFlow: Flow<Float> = context.dataStore.data.map { it[GRAPH_ANIMATION_SPEED] ?: 1.0f }
    val graphQualityFlow: Flow<String> = context.dataStore.data.map { it[GRAPH_QUALITY] ?: "high" }
    val graphGridFlow: Flow<Boolean> = context.dataStore.data.map { it[GRAPH_GRID] ?: true }
    val graphAxisLabelsFlow: Flow<Boolean> = context.dataStore.data.map { it[GRAPH_AXIS_LABELS] ?: true }
    
    val largeTextFlow: Flow<Boolean> = context.dataStore.data.map { it[LARGE_TEXT] ?: false }
    val hapticFeedbackFlow: Flow<Boolean> = context.dataStore.data.map { it[HAPTIC_FEEDBACK] ?: true }
    val soundFeedbackFlow: Flow<Boolean> = context.dataStore.data.map { it[SOUND_FEEDBACK] ?: false }
    
    val recentConvertersFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[RECENT_CONVERTERS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }
    val recentCalculatorsFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[RECENT_CALCULATORS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    suspend fun updateTheme(value: String) {
        context.dataStore.edit { it[THEME] = value }
    }

    suspend fun updateAccentColor(value: String) {
        context.dataStore.edit { it[ACCENT_COLOR] = value }
    }

    suspend fun updateAmoledMode(value: Boolean) {
        context.dataStore.edit { it[AMOLED_MODE] = value }
    }

    suspend fun updateDecimalPrecision(value: Int) {
        context.dataStore.edit { it[DECIMAL_PRECISION] = value }
    }

    suspend fun updateScientificNotation(value: Boolean) {
        context.dataStore.edit { it[SCIENTIFIC_NOTATION] = value }
    }

    suspend fun updateThousandsSeparator(value: Boolean) {
        context.dataStore.edit { it[THOUSANDS_SEPARATOR] = value }
    }

    suspend fun updateAngleMode(value: String) {
        context.dataStore.edit { it[ANGLE_MODE] = value }
    }

    suspend fun updateHistoryLimit(value: Int) {
        context.dataStore.edit { it[HISTORY_LIMIT] = value }
    }

    suspend fun updateGraphAnimationSpeed(value: Float) {
        context.dataStore.edit { it[GRAPH_ANIMATION_SPEED] = value }
    }

    suspend fun updateGraphQuality(value: String) {
        context.dataStore.edit { it[GRAPH_QUALITY] = value }
    }

    suspend fun updateGraphGrid(value: Boolean) {
        context.dataStore.edit { it[GRAPH_GRID] = value }
    }

    suspend fun updateGraphAxisLabels(value: Boolean) {
        context.dataStore.edit { it[GRAPH_AXIS_LABELS] = value }
    }

    suspend fun updateLargeText(value: Boolean) {
        context.dataStore.edit { it[LARGE_TEXT] = value }
    }

    suspend fun updateHapticFeedback(value: Boolean) {
        context.dataStore.edit { it[HAPTIC_FEEDBACK] = value }
    }

    suspend fun updateSoundFeedback(value: Boolean) {
        context.dataStore.edit { it[SOUND_FEEDBACK] = value }
    }

    suspend fun addRecentConverter(category: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[RECENT_CONVERTERS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
            val updated = (listOf(category) + current.filter { it != category }).take(5)
            prefs[RECENT_CONVERTERS] = updated.joinToString(",")
        }
    }

    suspend fun addRecentCalculator(calcId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[RECENT_CALCULATORS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
            val updated = (listOf(calcId) + current.filter { it != calcId }).take(5)
            prefs[RECENT_CALCULATORS] = updated.joinToString(",")
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { it.clear() }
    }
}
