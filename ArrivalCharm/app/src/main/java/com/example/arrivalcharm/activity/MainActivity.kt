package com.example.arrivalcharm.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
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
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        startSplash()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mLocationRequest = LocationRequest.create().apply {
            priority = PRIORITY_HIGH_ACCURACY
        }

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

    private fun startLocationUpdate() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }

    private fun onLocationChanged(location: Location) {
        mLastLocation = location
    }


    private fun checkPermissionForLocation(): Boolean {
        return if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            // 권한이 없으므로 권한 요청 보내기
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate()
            } else {
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (checkPermissionForLocation()) startLocationUpdate()
        map = googleMap

        val seoul = LatLng(37.348, -121.9002)

        val markerOptions = MarkerOptions()
        markerOptions.position(seoul)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")

        map?.addMarker(markerOptions)

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
    }
}