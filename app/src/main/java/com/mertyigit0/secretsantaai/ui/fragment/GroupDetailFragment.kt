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
import com.mertyigit0.secretsantaai.data.model.User
import com.mertyigit0.secretsantaai.databinding.FragmentGroupDetailBinding
import com.mertyigit0.secretsantaai.ui.adapter.UserAdapter
import com.mertyigit0.secretsantaai.viewmodels.GroupDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!
    private val groupDetailViewModel: GroupDetailViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
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

        val groupId = arguments?.getString("groupId") ?: return
        groupDetailViewModel.getGroupDetails(groupId)

        userAdapter = UserAdapter(emptyList())
        binding.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
        groupDetailViewModel.groupDetails.observe(viewLifecycleOwner) { group ->
            group?.let {
                binding.groupName.text = it.groupName
                userAdapter.updateUsers(it.users)
            }
        }

        groupDetailViewModel.groupDetails.observe(viewLifecycleOwner) { group ->
            group?.let { nonNullGroup ->
                binding.groupName.text = nonNullGroup.groupName

                // Çekiliş yapılacak butonun tıklama işlemi
                binding.drawRaffleButton.setOnClickListener {
                    // Kullanıcıların listesine erişim
                    performDraw(nonNullGroup.users, groupId)
                }
            }
        }


    }

    private fun performDraw(users: List<User>, groupId: String) {
        val userIds = users.map { it.userId }
        val shuffledUserIds = userIds.shuffled()

        // Çekilişi yap
        for (i in userIds.indices) {
            val giverId = userIds[i]
            val recipientId = shuffledUserIds[(i + 1) % userIds.size]

            // Sonuçları Firestore'a kaydet
            firestore.collection("drawResults")
                .document(giverId)
                .set(mapOf("recipientId" to recipientId, "groupId" to groupId))
                .addOnSuccessListener {
                    // Çekiliş tamamlandı, kullanıcıyı DrawResultFragment'e yönlendir
                    findNavController().navigate(R.id.action_groupDetailFragment_to_drawResultFragment, Bundle().apply {
                        putString("groupId", groupId)
                    })
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
