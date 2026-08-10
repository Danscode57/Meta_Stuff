package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpatialAnchorDao {
    @Query("SELECT * FROM spatial_anchors ORDER BY timestamp DESC")
    fun getAllAnchors(): Flow<List<SpatialAnchorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnchor(anchor: SpatialAnchorEntity)

    @Update
    suspend fun updateAnchor(anchor: SpatialAnchorEntity)

    @Delete
    suspend fun deleteAnchor(anchor: SpatialAnchorEntity)

    @Query("DELETE FROM spatial_anchors")
    suspend fun clearAll()
}
