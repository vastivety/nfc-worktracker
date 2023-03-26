package ca.mcgill.nfcworktracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM history ORDER BY start_time ASC")
    fun getAll(): List<HistoryEntry>

    @Insert
    fun insert(entry: HistoryEntry)

    @Delete
    fun delete(user: HistoryEntry)
}
