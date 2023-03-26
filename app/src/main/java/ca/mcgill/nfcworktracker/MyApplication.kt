package ca.mcgill.nfcworktracker

import android.app.Application
import ca.mcgill.nfcworktracker.database.AppDatabase

class MyApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { AppDatabase.getDatabase(this) }
    val history by lazy { database.historyEntryDao() }
}
