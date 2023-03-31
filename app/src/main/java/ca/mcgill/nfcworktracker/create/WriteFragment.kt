package ca.mcgill.nfcworktracker.create

import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.create.Util.navigateFailure
import ca.mcgill.nfcworktracker.databinding.FragmentCreateWriteBinding
import java.io.IOException

class WriteFragment : Fragment(), NfcAdapter.ReaderCallback {

    private lateinit var nfcAdapter: NfcAdapter

    private var tag: Tag? = null
    private var failureMsg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateWriteBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            tag = arguments?.getParcelable(Util.EXTRA_DISCOVERED_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION") //checked SDK_INT above
            tag = arguments?.getParcelable(Util.EXTRA_DISCOVERED_TAG)
        }

        binding.pretendWriteSuccess.setOnClickListener {
            navigateAway(true)
        }
        binding.pretendWriteFail.setOnClickListener {
            navigateAway(false, "debug button \"pretend fail\" clicked.")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (tag == null) {
            navigateAway(false, "tag from bundle is null")
        } else {
            navigateAway(writeToTag(), failureMsg)
        }
    }

    private fun writeToTag(): Boolean {
        //get ndef connection to tag
        val ndefTag = Ndef.get(tag)
        try {
            ndefTag.connect()
        } catch (_: IOException) {
            failureMsg = "Connection to tag failed"
            return false
        }
        // pre-write checks
        if (!ndefTag.isConnected) {
            failureMsg = "Tag disconnected early"
            return false
        }
        if (!ndefTag.isWritable) {
            failureMsg = "Please make sure tag is writable"
            return false
        }
        // write
        val record = NdefRecord.createMime(getString(R.string.tag_mime), Util.createTagContent().encodeToByteArray())
        try {
            ndefTag.writeNdefMessage(NdefMessage(record))
        } catch (_: Exception) {
            failureMsg = "Tag write failed"
            return false
        }
        // verify
        val ndefMessageAfterWrite: NdefMessage
        try {
            ndefMessageAfterWrite = ndefTag.ndefMessage
        } catch (_: Exception) {
            failureMsg = "Tag verification read failed"
            return false
        }
        if (ndefMessageAfterWrite.records.none {
            it.toMimeType() == getString(R.string.tag_mime)
        }) {
            failureMsg = "Mime type wrong after write"
            return false
        }
        // verification successful
        return true
    }

    private fun navigateAway(writeSuccessful: Boolean, failureMessage: String? = null) {
        nfcAdapter.disableReaderMode(requireActivity())
        if (writeSuccessful) {
            findNavController().navigate(R.id.action_writeFragment_success)
        } else {
            navigateFailure(failureMessage, R.id.action_writeFragment_failure)
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        // Discovery of a new tag should not happen at this point!
    }

    override fun onResume() {
        super.onResume()
        // Just to make sure reader mode is actually enabled. Sadly, there is no way to check whether it actually is.
        nfcAdapter.enableReaderMode(
            requireActivity(),
            this,
            Util.nfcReaderModeFlags,
            null
        )
    }
}
