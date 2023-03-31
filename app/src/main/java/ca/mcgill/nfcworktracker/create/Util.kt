package ca.mcgill.nfcworktracker.create

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R

object Util {
    const val nfcReaderModeFlags: Int = NfcAdapter.FLAG_READER_NFC_A

    const val EXTRA_FAILURE_MESSAGE = "ca.mcgill.nfcworktracker.create.extra-failure-message"
    const val EXTRA_DISCOVERED_TAG = "ca.mcgill.nfcworktracker.create.extra-discovered-tag"

    fun createTagContent(): String {
        return "this could be a random ID in theory"
    }

    fun Fragment.navigateFailure(msg: String?, actionId: Int = R.id.failureFragment) {
        val extras = Bundle()
        extras.putString(EXTRA_FAILURE_MESSAGE, msg)
        findNavController().navigate(actionId, extras)
    }
}