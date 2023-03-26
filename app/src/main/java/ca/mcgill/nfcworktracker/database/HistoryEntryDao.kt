package ca.mcgill.nfcworktracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM history ORDER BY start_time ASC")
    suspend fun getAll(): List<HistoryEntry>

    @Insert
    suspend fun insert(entry: HistoryEntry)

    @Delete
    suspend fun delete(entry: HistoryEntry)
}
