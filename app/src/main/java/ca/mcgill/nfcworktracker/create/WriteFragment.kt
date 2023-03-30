package ca.mcgill.nfcworktracker.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ca.mcgill.nfcworktracker.R
import ca.mcgill.nfcworktracker.databinding.FragmentCreateWriteBinding

class WriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateWriteBinding.inflate(layoutInflater)

        binding.pretendWriteSuccess.setOnClickListener {
            findNavController().navigate(R.id.action_writeFragment_success)
        }
        binding.pretendWriteFail.setOnClickListener {
            findNavController().navigate(R.id.action_writeFragment_failure)
        }

        return binding.root
    }
}
