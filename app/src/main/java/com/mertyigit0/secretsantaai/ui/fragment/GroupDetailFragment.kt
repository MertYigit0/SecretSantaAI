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

@AndroidEntryPoint
class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!
    private val groupDetailViewModel: GroupDetailViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private lateinit var currentUser: User

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
            group?.let { nonNullGroup ->
                binding.groupName.text = nonNullGroup.groupName
                userAdapter.updateUsers(nonNullGroup.users)

                // Mevcut kullanıcıyı al
                val currentUser = nonNullGroup.users.find { it.userId == FirebaseAuth.getInstance().currentUser?.uid } ?: return@let

                // Eğer grup üyesi değilse buton metnini değiştir
                if (!nonNullGroup.users.contains(currentUser)) {
                    binding.drawRaffleButton.text = getString(R.string.group_not_member)
                    binding.drawRaffleButton.isEnabled = false
                } else {
                    // Çekiliş yapılacak butonun tıklama işlemi
                    val isDrawn = nonNullGroup.drawResults.isNotEmpty()
                    var buttonText = if (isDrawn) {
                        getString(R.string.show_result)
                    } else {
                        // Eğer grup kurucusu değilseniz, buton "Yönetici Bekleniyor" yazsın
                        if (currentUser.userId != nonNullGroup.createdBy) {
                            binding.drawRaffleButton.text = getString(R.string.waiting_for_admin)
                            binding.drawRaffleButton.isEnabled = false
                            return@let
                        } else {
                            getString(R.string.draw_raffle)
                        }
                    }

                    // Çekilişi başlatma
                    binding.drawRaffleButton.text = buttonText
                    binding.drawRaffleButton.isEnabled = true

                    binding.drawRaffleButton.setOnClickListener {
                        if (isDrawn) {
                            // Çekiliş sonucu gösterme
                            findNavController().navigate(R.id.action_groupDetailFragment_to_drawResultFragment, Bundle().apply {
                                putString("groupId", nonNullGroup.groupId)
                            })
                        } else {
                            // Çekilişi başlat
                            performDraw(nonNullGroup.users, nonNullGroup.groupId)
                        }
                    }
                }

                // Grup üyelerinin sayısı 3'ten azsa çekilişi başlatma
                if (nonNullGroup.users.size < 3) {
                    binding.drawRaffleButton.text = getString(R.string.minimum_three_members)
                    binding.drawRaffleButton.isEnabled = false
                }
            }
        }

        }


    private fun performDraw(users: List<User>, groupId: String) {
        val userIds = users.map { it.userId }
        val shuffledUserIds = userIds.shuffled()

        // Çekilişi yap
        val drawResults = mutableMapOf<String, String>()
        val usedRecipients = mutableSetOf<String>()

        for (i in userIds.indices) {
            val giverId = userIds[i]
            var recipientId: String

            // Random bir alıcı seç, ancak aynı kişiden hediye almasını engelle
            do {
                recipientId = shuffledUserIds[(i + 1) % userIds.size]
            } while (usedRecipients.contains(recipientId) || recipientId == giverId)

            // Sonuçları haritaya ekle ve alıcıyı "kullanılmış" olarak işaretle
            drawResults[giverId] = recipientId
            usedRecipients.add(recipientId)
        }

        // Çekiliş sonuçlarını Firestore'a kaydet
        FirebaseFirestore.getInstance().collection("groups")
            .document(groupId)
            .update("drawResults", drawResults)
            .addOnSuccessListener {
                // Çekiliş tamamlandı
                findNavController().navigate(R.id.action_groupDetailFragment_to_drawResultFragment, Bundle().apply {
                    putString("groupId", groupId)
                })
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

