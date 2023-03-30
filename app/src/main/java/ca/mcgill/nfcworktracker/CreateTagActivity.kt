package ca.mcgill.nfcworktracker

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.create_tag_fragment_container)
        when (navController.currentDestination?.id) {
            // Prevent going back to write fragment
            R.id.confirmationFragment -> this.finish()
            // Follow retry path.
            R.id.failureFragment -> navController.navigate(R.id.action_failureFragment_retry)
            // Default behaviour: navigate up. If no previous fragment is found, return to previous activity.
            else -> {
                if(!navController.popBackStack()) {
                    this.finish()
                }
            }
        }
    }
}
