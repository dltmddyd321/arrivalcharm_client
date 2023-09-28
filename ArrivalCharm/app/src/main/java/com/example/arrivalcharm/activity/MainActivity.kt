package com.example.arrivalcharm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.databinding.ActivityMainBinding
import com.example.arrivalcharm.datamodel.Location
import com.example.arrivalcharm.db.room.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goLoginBtn.setOnClickListener {
            val location = Location(
                2,
                "경기도 광주시 중앙로 22번길 14-16 12791",
                "46.2344",
                "185.5434",
                System.currentTimeMillis()
            )
            CoroutineScope(Dispatchers.IO).launch { locationViewModel.insertLocation(location = location) }

//            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.checkDB.setOnClickListener {
            lifecycleScope.launch {
                val list = locationViewModel.getAllLocation()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "${list.size}", Toast.LENGTH_LONG).show()
                }
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
}