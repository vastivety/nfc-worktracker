package ca.mcgill.nfcworktracker.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.databinding.FragmentCreateFailureBinding

class FailureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateFailureBinding.inflate(layoutInflater)

        binding.next.setOnClickListener {
            activity?.finish()
        }
        binding.retry.setOnClickListener {
            findNavController().navigate(R.id.action_failureFragment_retry)
        }

        return binding.root
    }
}
