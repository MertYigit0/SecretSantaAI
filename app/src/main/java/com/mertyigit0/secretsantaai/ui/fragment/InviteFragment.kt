package com.mertyigit0.secretsantaai.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.databinding.FragmentInviteBinding
import com.mertyigit0.secretsantaai.ui.adapter.GroupAdapter
import com.mertyigit0.secretsantaai.viewmodels.InviteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InviteFragment : Fragment() {

    private var _binding: FragmentInviteBinding? = null
    private val binding get() = _binding!!
    private val inviteViewModel: InviteViewModel by viewModels()
    private val groupAdapter = GroupAdapter()
    @Inject
    lateinit var auth: FirebaseAuth
    private var selectedGroup: Group? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.groupsRecyclerView.adapter = groupAdapter
    }

    private fun observeViewModel() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            inviteViewModel.loadUserGroups(userId)
        } else {
            showToast(R.string.user_not_logged_in)
        }

        inviteViewModel.userGroups.observe(viewLifecycleOwner) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    private fun setupListeners() {
        groupAdapter.setOnItemClickListener { group ->
            selectedGroup = group
        }

        binding.generateQrButton.setOnClickListener {
            selectedGroup?.let { group ->
                generateQrCode(group)
            } ?: showToast(R.string.please_select_group)
        }
    }

    private fun generateQrCode(group: Group) {
        try {
            val qrCodeData = group.groupId
            val bitmap = createQrCodeBitmap(qrCodeData)
            binding.qrCodeImageView.setImageBitmap(bitmap)
            showToast(R.string.qr_code_generated, group.groupName)
        } catch (e: Exception) {
            showToast(R.string.error_generating_qr_code)
        }
    }

    private fun createQrCodeBitmap(data: String): Bitmap {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    private fun showToast(messageResId: Int, vararg formatArgs: Any) {
        Toast.makeText(requireContext(), getString(messageResId, *formatArgs), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

