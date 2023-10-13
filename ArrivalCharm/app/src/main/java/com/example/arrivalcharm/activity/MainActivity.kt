package com.example.arrivalcharm.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.R
import com.example.arrivalcharm.databinding.ActivityMainBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.db.room.LocationViewModel
import com.example.arrivalcharm.util.RequestPermissionUtil
import com.example.arrivalcharm.util.RequestPermissionUtil.Companion.REQUEST_LOCATION
import com.example.arrivalcharm.util.RequestPermissionUtil.Companion.permissionsLocationDownApi29Impl
import com.example.arrivalcharm.util.RequestPermissionUtil.Companion.permissionsLocationUpApi29Impl
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private lateinit var splash: SplashScreen
    private lateinit var binding: ActivityMainBinding
    private val locationViewModel: LocationViewModel by viewModels()
    private val dataStoreViewModel: DatastoreViewModel by viewModels()
    private var lat = 0.0
    private var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        getLocation()

        binding.goLoginBtn.setOnClickListener {
//            val location = Location(
//                2,
//                "경기도 광주시 중앙로 22번길 14-16 12791",
//                "46.2344",
//                "185.5434",
//                System.currentTimeMillis()
//            )
//            CoroutineScope(Dispatchers.IO).launch { locationViewModel.insertLocation(location = location) }

            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.checkDB.setOnClickListener {
            lifecycleScope.launch {
                val list = locationViewModel.getAllLocation()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "${list.size}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.checkPrefs.setOnClickListener {
//            dataStoreViewModel.putAuthToken("fgjhbyfhauidw-r432adaAffggd430as")
            lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                Toast.makeText(this@MainActivity, token, Toast.LENGTH_LONG).show()
            }
        }

        //TODO: 추후 SplashAcitvity로 옮겨질 예정
//        lifecycleScope.launch {
//            adviceViewModel.fetchAdvice().collect { result ->
//                when (result) {
//                    is ApiResult.Success -> Toast.makeText(this@MainActivi₩ty, result.data, Toast.LENGTH_LONG).show()
//                    is ApiResult.Error -> Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val seoul = LatLng(37.556, 126.97)

        val markerOptions = MarkerOptions()
        markerOptions.position(seoul)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")

        map?.addMarker(markerOptions)

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
    }

    private fun getLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsLocationUpApi29Impl,
                    REQUEST_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsLocationDownApi29Impl,
                    REQUEST_LOCATION
                )
            }
        } else {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { success: Location? ->
                    success?.let { location ->
                        lat = location.latitude
                        lon = location.longitude
                        Toast.makeText(this, "lat : $lat / lon : $lon", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { fail ->
                    Toast.makeText(this, fail.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }
}