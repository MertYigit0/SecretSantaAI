package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentTabBinding
import com.mertyigit0.secretsantaai.ui.adapter.TabAdapter

class TabFragment : Fragment() {

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewPager ve TabLayout ayarlaması
        val adapter = TabAdapter(this)
        binding.viewPager.adapter = adapter

        // Tab isimlerini ve sıralarını belirtiyoruz
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.join_group_tab)  // İlk fragment
                1 -> getString(R.string.invite_tab)     // İkinci fragment
                else -> getString(R.string.tab_default, position + 1)  // Diğer fragmentler
            }
        }.attach()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
