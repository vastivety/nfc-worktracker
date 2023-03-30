package ca.mcgill.nfcworktracker.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.databinding.FragmentCreateSetupBinding

class SetupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateSetupBinding.inflate(layoutInflater)

        binding.pretendDiscovered.setOnClickListener {
            findNavController().navigate(R.id.action_setupFragment_to_writeFragment)
        }

        return binding.root
    }
}
