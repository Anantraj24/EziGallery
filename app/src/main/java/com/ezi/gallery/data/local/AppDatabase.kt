package com.ezi.gallery.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaId: Long,
    val contentUriString: String,
    val dateAdded: Long = System.currentTimeMillis()
)

@Dao
interface FavoriteDao {
    @Query("SELECT mediaId FROM favorites")
    fun getAllFavoriteIdsFlow(): Flow<List<Long>>

    @Query("SELECT * FROM favorites ORDER BY dateAdded DESC")
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
    fun isFavoriteFlow(mediaId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
    suspend fun isFavorite(mediaId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
    suspend fun deleteFavorite(mediaId: Long)

    @Query("DELETE FROM favorites WHERE mediaId IN (:mediaIds)")
    suspend fun deleteFavorites(mediaIds: List<Long>)
}

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
