package ca.mcgill.nfcworktracker

import android.app.Application
import ca.mcgill.nfcworktracker.database.AppDatabase
import com.google.android.material.color.DynamicColors

class MyApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { AppDatabase.getDatabase(this) }
    val history by lazy { database.historyEntryDao() }

    override fun onCreate() {
        super.onCreate()
        // Apply material 3 dynamic colors
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
