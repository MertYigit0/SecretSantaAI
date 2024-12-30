package com.mertyigit0.secretsantaai.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mertyigit0.secretsantaai.ui.fragment.InviteFragment
import com.mertyigit0.secretsantaai.ui.fragment.JoinGroupFragment

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> JoinGroupFragment() // JoinGroupFragment
            1 -> InviteFragment()    // InviteFragment
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
