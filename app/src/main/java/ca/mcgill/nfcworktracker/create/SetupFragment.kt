package ca.mcgill.nfcworktracker.create

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.create.Util.navigateFailure
import ca.mcgill.nfcworktracker.databinding.FragmentCreateSetupBinding

class SetupFragment : Fragment(), NfcAdapter.ReaderCallback {

    private lateinit var nfcAdapter: NfcAdapter

    private var doNotStopReaderModeOnPause = false

    private var discoveredTag: Tag? = null
    private val onTagDiscoveredRunnable = Runnable {
        if (discoveredTag == null) {
            navigateFailure("discovered null tag", R.id.action_setupFragment_failure)
            return@Runnable
        }
        //TODO check if ndef writable
        //TODO check if has content

        val tagInfo = Bundle()
        tagInfo.putParcelable(Util.EXTRA_DISCOVERED_TAG, discoveredTag)
        doNotStopReaderModeOnPause = true
        findNavController().navigate(R.id.action_setupFragment_next, tagInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateSetupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onTagDiscovered(tag: Tag?) {
        discoveredTag = tag
        Handler(requireContext().mainLooper).post(onTagDiscoveredRunnable)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableReaderMode(
            requireActivity(),
            this,
            Util.nfcReaderModeFlags,
            null
        )
    }

    override fun onPause() {
        if (!doNotStopReaderModeOnPause) {
            nfcAdapter.disableReaderMode(requireActivity())
        }
        super.onPause()
    }
}
