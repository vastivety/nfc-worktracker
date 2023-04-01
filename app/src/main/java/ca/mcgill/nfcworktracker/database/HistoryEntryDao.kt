package ca.mcgill.nfcworktracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM history ORDER BY start_time DESC")
    suspend fun getAllDescending(): List<HistoryEntry>

    @Insert
    suspend fun insert(entry: HistoryEntry)

    @Delete
    suspend fun delete(entry: HistoryEntry)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
