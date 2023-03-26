package ca.mcgill.nfcworktracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * data point for a single history entry.
 */
@Entity(tableName = "history")
class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "start_time") val startTime: Instant?,
    val endTime: Instant?
    ) {

    override fun toString(): String {
        return "recorded from $startTime to $endTime."
    }
}
