package com.example.arrivalcharm.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.databinding.ActivityMainBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.util.PermissionUtil
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var splash: SplashScreen
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissionForLocation()
    }

    private fun mainStart(lat: Double, lng: Double) {
        lifecycleScope.launch {
//            if (dataStoreViewModel.isValidLogin()) {
//                val intent = Intent(this@MainActivity, HomeActivity::class.java)
//                intent.putExtra("lat", lat)
//                intent.putExtra("lng", lng)
//                startActivity(intent)
//            } else {
//                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//            }
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            startActivity(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun startSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashView ->
                val icon = splashView.iconView ?: return@setOnExitAnimationListener

                ObjectAnimator.ofPropertyValuesHolder(icon).run {
                    interpolator = AnticipateInterpolator()
                    repeatCount = 2 // 반복 횟수
                    duration = 500L
                    doOnEnd {
                        splashView.remove()
                    }
                    start()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                if (it == null) {
                    Toast.makeText(this, "위치 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                mainStart(it.latitude, it.longitude)
//                val address = getAddress()
//                if (address.isNotEmpty()) {
//                    val mainAddress = address.first()
////                    Toast.makeText(
////                        this,
////                        "lat : ${address.first().adminArea} / log : ${address.first().locality} / ${mainAddress.thoroughfare} / ${mainAddress.featureName}",
////                        Toast.LENGTH_SHORT
//
//                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "위치 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissionForLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
            return
        }
        ActivityCompat.requestPermissions(
            this,
            PermissionUtil().getPermissions(),
            PermissionUtil.REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAddress(lat: Double, lng: Double): List<Address> {
        val address = mutableListOf<Address>()
        try {
            val geocoder = Geocoder(this, Locale.KOREA)
            geocoder.getFromLocation(lat, lng, 1)?.let { address.addAll(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}