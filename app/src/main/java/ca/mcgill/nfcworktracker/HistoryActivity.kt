package ca.mcgill.nfcworktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.mcgill.nfcworktracker.databinding.ActivityHistoryBinding
import ca.mcgill.nfcworktracker.history.HistoryAdapter
import ca.mcgill.nfcworktracker.history.HistoryDataPoint
import ca.mcgill.nfcworktracker.history.HistoryDatabaseHelper
import java.time.Instant

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = HistoryDatabaseHelper(application as MyApplication)

        //populate with some example values
        //TODO remove
        dbHelper.deleteAll()
        dbHelper.add(HistoryDataPoint(Instant.now().minusSeconds(400), Instant.now()))

        binding.recyclerView.adapter = HistoryAdapter(dbHelper.getAll())
    }
}
