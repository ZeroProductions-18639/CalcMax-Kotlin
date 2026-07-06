package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val category: String = "calculator" // e.g., standard, programmer, finance, health
)

@Entity(tableName = "graphs")
data class GraphEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val expression: String,
    val colorHex: String = "#2196F3",
    val isVisible: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "calculator", "converter", "graph", "formula", "constant"
    val targetId: String, // e.g. converter category, formula name, constant name, or calculator type
    val value: String = "", // secondary description or expression if any
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE expression LIKE :query OR result LIKE :query ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()
}

@Dao
interface GraphDao {
    @Query("SELECT * FROM graphs ORDER BY timestamp DESC")
    fun getAllGraphs(): Flow<List<GraphEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGraph(graph: GraphEntity): Long

    @Update
    suspend fun updateGraph(graph: GraphEntity)

    @Delete
    suspend fun deleteGraph(graph: GraphEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND targetId = :targetId)")
    fun isFavorite(type: String, targetId: String): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE title LIKE :query OR targetId LIKE :query ORDER BY timestamp DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE type = :type AND targetId = :targetId")
    suspend fun deleteFavorite(type: String, targetId: String)

    @Delete
    suspend fun deleteFavoriteEntity(favorite: FavoriteEntity)
}
