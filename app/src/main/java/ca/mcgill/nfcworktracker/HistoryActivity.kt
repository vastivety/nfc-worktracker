package ca.mcgill.nfcworktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.mcgill.nfcworktracker.databinding.ActivityHistoryBinding
import ca.mcgill.nfcworktracker.historydata.HistoryAdapter
import ca.mcgill.nfcworktracker.historydata.HistoryDataPoint

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = HistoryAdapter(arrayOf(HistoryDataPoint(), HistoryDataPoint(), HistoryDataPoint()))
    }
}
