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
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.DialogJoinGroupCustomBinding
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
                Toast.makeText(requireContext(), getString(R.string.group_join_success), Toast.LENGTH_SHORT).show()
            }

            result.onFailure {
                Toast.makeText(requireContext(), getString(R.string.group_join_failure, it.message), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showJoinGroupDialog() {
        val dialogBinding = DialogJoinGroupCustomBinding.inflate(layoutInflater)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnDialogJoin.setOnClickListener {
            val groupId = dialogBinding.etGroupId.text.toString().trim()
            if (groupId.isNotEmpty()) {
                joinGroupViewModel.joinGroup(groupId)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.valid_group_id_required), Toast.LENGTH_SHORT).show()

            }
        }

        dialog.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
