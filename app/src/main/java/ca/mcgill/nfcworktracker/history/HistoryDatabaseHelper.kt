package ca.mcgill.nfcworktracker.history

import ca.mcgill.nfcworktracker.MyApplication
import ca.mcgill.nfcworktracker.database.HistoryEntry
import ca.mcgill.nfcworktracker.database.HistoryEntryDao
import kotlinx.coroutines.runBlocking
import java.time.Instant

class HistoryDatabaseHelper(application: MyApplication) {
    private val historyDao: HistoryEntryDao
    init {
        historyDao = application.history
    }

    fun getAllDescending(): Array<HistoryDataPoint> {
        val list = ArrayList<HistoryDataPoint>()
        for (historyEntry: HistoryEntry in runBlocking { historyDao.getAllDescending() }) {
            list.add(HistoryDataPoint(
                Instant.ofEpochSecond(historyEntry.startTime),
                Instant.ofEpochSecond(historyEntry.endTime)
            ))
        }
        return list.toTypedArray()
    }

    fun insertIntoDatabase(point: HistoryDataPoint) {
        val historyEntry = HistoryEntry(
            0,
            point.startTime.epochSecond,
            point.endTime.epochSecond
        )
        runBlocking { historyDao.insert(historyEntry) }
    }

    fun deleteAll() {
        runBlocking { historyDao.deleteAll() }
    }
}
