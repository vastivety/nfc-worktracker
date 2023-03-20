package ca.mcgill.nfcworktracker

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class TagDiscoveredActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = Intent(applicationContext, NfcService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            service.putExtra(NfcAdapter.EXTRA_TAG, intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java))
            service.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java))
        } else {
            @Suppress("DEPRECATION")
            service.putExtra(NfcAdapter.EXTRA_TAG, intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?)
            @Suppress("DEPRECATION")
            service.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))
        }
        applicationContext.startForegroundService(service)
        finish()
    }
}
