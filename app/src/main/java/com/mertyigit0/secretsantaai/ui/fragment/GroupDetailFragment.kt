package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.model.User
import com.mertyigit0.secretsantaai.databinding.FragmentGroupDetailBinding
import com.mertyigit0.secretsantaai.ui.adapter.UserAdapter
import com.mertyigit0.secretsantaai.viewmodels.GroupDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!
    private val groupDetailViewModel: GroupDetailViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupId = arguments?.getString("groupId") ?: return

        setupRecyclerView()
        observeGroupDetails(groupId)
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(emptyList())
        binding.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun observeGroupDetails(groupId: String) {
        groupDetailViewModel.getGroupDetails(groupId)
        groupDetailViewModel.groupDetails.observe(viewLifecycleOwner) { group ->
            group?.let { updateUI(it) }
        }
    }

    private fun updateUI(group: Group) {
        binding.groupName.text = group.groupName
        userAdapter.updateUsers(group.users)
        val currentUser = group.users.find { it.userId == firebaseAuth.currentUser?.uid }
        if (currentUser == null || group.users.size < 3) {
            disableRaffleButton(R.string.minimum_three_members)
            return
        }

        setupRaffleButton(group, currentUser)
    }

    private fun setupRaffleButton(group: Group, currentUser: User) {
        val isDrawn = group.drawResults.isNotEmpty()
        val buttonText = when {
            !group.users.contains(currentUser) -> R.string.group_not_member
            isDrawn -> R.string.show_result
            currentUser.userId != group.createdBy -> R.string.waiting_for_admin
            else -> R.string.draw_raffle
        }

        binding.drawRaffleButton.apply {
            text = getString(buttonText)
            isEnabled = buttonText == R.string.draw_raffle || isDrawn
            setOnClickListener {
                if (isDrawn) showDrawResult(group.groupId) else performDraw(group.users, group.groupId)
            }
        }
    }

    private fun disableRaffleButton(messageResId: Int) {
        binding.drawRaffleButton.apply {
            text = getString(messageResId)
            isEnabled = false
        }
    }

    private fun showDrawResult(groupId: String) {
        findNavController().navigate(R.id.action_groupDetailFragment_to_drawResultFragment, Bundle().apply {
            putString("groupId", groupId)
        })
    }

    private fun performDraw(users: List<User>, groupId: String) {
        val userIds = users.map { it.userId }
        val shuffledUserIds = userIds.shuffled()
        val drawResults = mutableMapOf<String, String>()
        val usedRecipients = mutableSetOf<String>()

        userIds.forEachIndexed { i, giverId ->
            var recipientId: String
            do {
                recipientId = shuffledUserIds[(i + 1) % userIds.size]
            } while (usedRecipients.contains(recipientId) || recipientId == giverId)

            drawResults[giverId] = recipientId
            usedRecipients.add(recipientId)
        }

        saveDrawResults(groupId, drawResults)
    }

    private fun saveDrawResults(groupId: String, drawResults: Map<String, String>) {
        FirebaseFirestore.getInstance().collection("groups")
            .document(groupId)
            .update("drawResults", drawResults)
            .addOnSuccessListener {
                showDrawResult(groupId)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
