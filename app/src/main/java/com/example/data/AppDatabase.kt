package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "avatar_generations")
data class AvatarGeneration(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val faceName: String,
    val bodyType: String,
    val scenarioName: String,
    val scenarioPrompt: String,
    val styleName: String,
    val lightingName: String,
    val finalPrompt: String,
    val imageUriOrBase64: String, // Base64 encoded or placeholder image
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "custom_faces")
data class CustomFace(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUri: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AvatarDao {
    @Query("SELECT * FROM avatar_generations ORDER BY timestamp DESC")
    fun getAllGenerations(): Flow<List<AvatarGeneration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(generation: AvatarGeneration)

    @Query("DELETE FROM avatar_generations WHERE id = :id")
    suspend fun deleteGeneration(id: Int)

    @Query("UPDATE avatar_generations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM custom_faces ORDER BY timestamp DESC")
    fun getAllCustomFaces(): Flow<List<CustomFace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomFace(face: CustomFace)

    @Query("DELETE FROM custom_faces WHERE id = :id")
    suspend fun deleteCustomFace(id: Int)
}

@Database(entities = [AvatarGeneration::class, CustomFace::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avatarDao(): AvatarDao
}
