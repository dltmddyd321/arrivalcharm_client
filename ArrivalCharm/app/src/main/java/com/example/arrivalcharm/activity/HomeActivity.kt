package com.example.arrivalcharm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.arrivalcharm.R
import com.example.arrivalcharm.databinding.ActivityHomeBinding
import com.example.arrivalcharm.fragment.HomeFragment
import com.example.arrivalcharm.fragment.MapFragment
import com.example.arrivalcharm.fragment.SettingFragment
import com.example.arrivalcharm.util.DistanceManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            } else {
                backToast.show()
                backPressedTime = System.currentTimeMillis()
            }
        }
    }

    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var settingFragment: SettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backToast = Toast.makeText(this, "한 번 더 뒤로가기 하시면 종료됩니다.", Toast.LENGTH_SHORT)

        val lat = intent?.getDoubleExtra("lat", 0.0) ?: DistanceManager.DefaultLat
        val lng = intent?.getDoubleExtra("lng", 0.0) ?: DistanceManager.DefaultLng
        val address = intent?.getStringExtra("address") ?: ""

        homeFragment = HomeFragment.newInstance(address, lat, lng)
        mapFragment = MapFragment.newInstance(lat, lng)
        settingFragment = SettingFragment()

        supportFragmentManager.beginTransaction().add(R.id.tabFrame, homeFragment).commit()

        binding.mainTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceTabView(homeFragment)
                    1 -> replaceTabView(mapFragment)
                    2 -> replaceTabView(settingFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun replaceTabView(selectedTab: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.tabFrame, selectedTab).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        backToast.cancel()
    }
}