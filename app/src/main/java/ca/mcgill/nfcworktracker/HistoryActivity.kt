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

        val adapter = HistoryAdapter(HistoryDatabaseHelper(application as MyApplication))
        binding.recyclerView.adapter = adapter

        binding.clearTestButton.setOnClickListener { adapter.clearHistory() }
        binding.addTestButton.setOnClickListener { adapter.addPoint(
            HistoryDataPoint(Instant.now().minusSeconds(400), Instant.now())
        ) }

    }
}
