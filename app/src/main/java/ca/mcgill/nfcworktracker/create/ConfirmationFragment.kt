package ca.mcgill.nfcworktracker.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        return binding.root
    }
}
