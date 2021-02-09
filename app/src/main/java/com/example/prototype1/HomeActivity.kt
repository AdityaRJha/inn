package com.example.prototype1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.prototype1.fragments.ChatFragment
import com.example.prototype1.fragments.HomeFragment
import com.example.prototype1.fragments.StatusFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var sectionsPagerAdapter: SectionPagerAdapter? = null
    private val homeFragment = HomeFragment()
    private val statusFragment = StatusFragment()
    private val chatFragment = ChatFragment()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sectionsPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        if(userId == null) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
    }

    fun onLogout(v: View){
        firebaseAuth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    inner class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getCount() = 3

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> homeFragment
                1 -> statusFragment
                else -> chatFragment
            }
        }

    }

    companion object{
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}