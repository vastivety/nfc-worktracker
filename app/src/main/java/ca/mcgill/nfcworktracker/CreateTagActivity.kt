package ca.mcgill.nfcworktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.navigation.findNavController
import ca.mcgill.nfcworktracker.databinding.ActivityCreateTagBinding

class CreateTagActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCreateTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                // Navigate back. If no previous fragment is found, return to previous activity.
                if(!findNavController(R.id.create_tag_fragment_container).popBackStack()) {
                    this.finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
