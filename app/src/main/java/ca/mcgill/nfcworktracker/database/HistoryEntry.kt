package ca.mcgill.nfcworktracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * data point for a single history entry.
 * startTime & endTime are seconds from epoch 1970-01-01T00:00:00Z
 */
@Entity(tableName = "history")
class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "start_time") val startTime: Long,
    val endTime: Long
    )
