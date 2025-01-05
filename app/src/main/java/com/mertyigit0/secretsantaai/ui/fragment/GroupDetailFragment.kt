package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertyigit0.secretsantaai.databinding.FragmentGroupDetailBinding
import com.mertyigit0.secretsantaai.ui.adapter.UserAdapter
import com.mertyigit0.secretsantaai.viewmodels.GroupDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!
    private val groupDetailViewModel: GroupDetailViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter(emptyList())
        binding.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        val groupId = arguments?.getString("groupId") ?: return
        groupDetailViewModel.getGroupDetails(groupId)

        groupDetailViewModel.groupDetails.observe(viewLifecycleOwner) { group ->
            group?.let {
                binding.groupName.text = it.groupName
                userAdapter.updateUsers(it.users)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
