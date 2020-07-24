package com.nexters.mytine.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nexters.mytine.ui.home.restrospect.RetrospectFragment
import com.nexters.mytine.ui.home.routine.RoutineFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RoutineFragment()
            1 -> RetrospectFragment()
            else -> RetrospectFragment()
        }
    }
}
