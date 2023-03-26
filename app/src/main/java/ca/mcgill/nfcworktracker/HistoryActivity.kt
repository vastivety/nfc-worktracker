package ca.mcgill.nfcworktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.mcgill.nfcworktracker.database.HistoryEntry
import ca.mcgill.nfcworktracker.databinding.ActivityHistoryBinding
import ca.mcgill.nfcworktracker.history.HistoryAdapter
import ca.mcgill.nfcworktracker.history.HistoryDatabaseHelper
import kotlinx.coroutines.runBlocking

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = HistoryDatabaseHelper(application as MyApplication)
        binding.recyclerView.adapter = HistoryAdapter(dbHelper.getAll())
    }
}
