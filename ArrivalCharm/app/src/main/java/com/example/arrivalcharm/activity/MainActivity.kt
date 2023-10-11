package com.example.arrivalcharm.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.R
import com.example.arrivalcharm.databinding.ActivityMainBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.db.room.LocationViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splash = installSplashScreen()
        startSplash()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

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

        val SEOUL = LatLng(37.556, 126.97)

        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")

        map?.addMarker(markerOptions)

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10f))
    }


//    private fun startSplash() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            splashScreen.setOnExitAnimationListener { splashView ->
//                val fadeIn = AlphaAnimation(0f, 1f)
//                fadeIn.duration = 1000
//
//                val fadeOut = AlphaAnimation(1f, 0f)
//                fadeOut.startOffset = 1000
//                fadeOut.duration = 1000
//
//                val animationSet = AnimationSet(true)
//                animationSet.addAnimation(fadeIn)
//                animationSet.addAnimation(fadeOut)
//
//                splashView.iconView?.startAnimation(animationSet)
//
//                animationSet.setAnimationListener(object : Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation?) {
//                    }
//
//                    override fun onAnimationEnd(animation: Animation?) {
//                        splashView.remove()
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation?) {}
//                })
//            }
//        }
//    }

//    @SuppressLint("Recycle")
//    private fun startSplash() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            splashScreen.setOnExitAnimationListener { splashView ->
//                val icon = splashView.iconView ?: return@setOnExitAnimationListener
//                icon.rotation = 90f
//
//                ObjectAnimator.ofFloat(
//                    icon,
//                    "rotation",
//                    0f
//                ).apply {
//                    interpolator = AnticipateInterpolator()
//                    duration = 1000L
//                    doOnEnd {
//                        splashView.remove()
//                    }
//                    start()
//                }
//            }
//        }
//    }
}