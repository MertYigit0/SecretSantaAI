package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mertyigit0.secretsantaai.databinding.FragmentJoinGroupBinding
import com.mertyigit0.secretsantaai.viewmodels.JoinGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGroupFragment : Fragment() {

    private var _binding: FragmentJoinGroupBinding? = null
    private val binding get() = _binding!!
    private val joinGroupViewModel: JoinGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJoinGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            showJoinGroupDialog()
        }

        joinGroupViewModel.joinGroupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Successfully joined the group", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Failed to join the group: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showJoinGroupDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Join Group")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val groupIdInput = EditText(requireContext()).apply {
            hint = "Group ID"
        }

        layout.addView(groupIdInput)
        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val groupId = groupIdInput.text.toString().trim()

            if (groupId.isNotEmpty()) {
                joinGroupViewModel.joinGroup(groupId)
            } else {
                Toast.makeText(requireContext(), "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
