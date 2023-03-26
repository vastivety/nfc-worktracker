package ca.mcgill.nfcworktracker.history

import ca.mcgill.nfcworktracker.MyApplication
import ca.mcgill.nfcworktracker.database.HistoryEntry
import ca.mcgill.nfcworktracker.database.HistoryEntryDao
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.temporal.TemporalAccessor

class HistoryDatabaseHelper(application: MyApplication) {
    private val historyDao: HistoryEntryDao
    init {
        historyDao = application.history
    }

    fun getAll(): Array<HistoryDataPoint> {
        val list = ArrayList<HistoryDataPoint>()
        for (historyEntry: HistoryEntry in runBlocking { historyDao.getAll() }) {
            list.add(HistoryDataPoint(
                Instant.ofEpochSecond(historyEntry.startTime),
                Instant.ofEpochSecond(historyEntry.endTime)
            ))
        }
        return list.toTypedArray()
    }
}
