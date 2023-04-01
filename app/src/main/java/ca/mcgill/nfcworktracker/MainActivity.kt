package ca.mcgill.nfcworktracker

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import ca.mcgill.nfcworktracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nfcServiceObserverListener: NfcService.Observer.Listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        binding.createNew.setOnClickListener {
            startActivity(Intent(this, CreateTagActivity::class.java))
        }

        binding.showPrevious.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        nfcServiceObserverListener = NfcService.Observer.addChangeListener(
            true,
            object : NfcService.Observer.Listener {
                override fun callback(newStatus: Boolean) {
                    binding.createNew.compoundDrawables[1].setTint(
                        if (newStatus)
                            getColor(R.color.home_icon_active)
                        else
                            getColor(R.color.home_icon_inactive)
                    )
                }
            })
    }

    override fun onPause() {
        nfcServiceObserverListener.remove()
        super.onPause()
    }
}
