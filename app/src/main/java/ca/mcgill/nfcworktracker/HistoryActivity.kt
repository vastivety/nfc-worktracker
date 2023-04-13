package ca.mcgill.nfcworktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.mcgill.nfcworktracker.databinding.ActivityHistoryBinding
import ca.mcgill.nfcworktracker.history.HistoryAdapter
import ca.mcgill.nfcworktracker.history.HistoryDatabaseHelper

class HistoryActivity : AppCompatActivity() {
    private lateinit var adapter: HistoryAdapter

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var nfcServiceObserverListener: NfcService.Observer.Listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HistoryAdapter(HistoryDatabaseHelper(application as MyApplication))
        binding.recyclerView.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        nfcServiceObserverListener = NfcService.Observer.addChangeListener(
            true,
            object : NfcService.Observer.Listener {
                override fun callback(newStatus: Boolean) {
                    adapter.notifyNfcServiceStatusChanged(newStatus)
                }
            })
    }

    override fun onPause() {
        nfcServiceObserverListener.remove()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.history_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_history -> {
                adapter.clearHistory()
                true
            }
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
