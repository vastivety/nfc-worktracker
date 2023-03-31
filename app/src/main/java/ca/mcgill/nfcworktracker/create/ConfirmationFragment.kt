package ca.mcgill.nfcworktracker.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.databinding.FragmentCreateConfirmationBinding

class ConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateConfirmationBinding.inflate(layoutInflater)

        binding.next.setOnClickListener {
            activity?.finish()
        }

        binding.createConfirmationText.text = getString(R.string.create_confirmation_text_placeholder, binding.next.text)

        return binding.root
    }
}
