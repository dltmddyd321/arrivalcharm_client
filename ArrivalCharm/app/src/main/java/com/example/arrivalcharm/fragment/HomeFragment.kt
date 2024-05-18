package com.example.arrivalcharm.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.activity.PagingTestActivity
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentHomeBinding
import com.example.arrivalcharm.datamodel.Location
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.AdviceViewModel
import com.example.arrivalcharm.viewmodel.FetchRecentViewModel
import com.example.arrivalcharm.viewmodel.SaveDestinationViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.ln

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val dataStoreViewModel: DatastoreViewModel by activityViewModels()

    @NetworkModule.Advice
    private val adviceViewModel: AdviceViewModel by activityViewModels()

    @NetworkModule.Main
    private val fetchRecentViewModel: FetchRecentViewModel by activityViewModels()

    @NetworkModule.Main
    private val saveDestinationViewModel: SaveDestinationViewModel by activityViewModels()

    companion object {
        fun newInstance(address: String, lat: Double, lng: Double): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("address", address)
            args.putDouble("lat", lat)
            args.putDouble("lng", lng)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adviceViewModel.fetchAdvice().onEach { result ->
            when (result) {
                is ApiResult.Success -> {
                    val advice = result.data
                    binding.wiseText.text = advice
                }

                is ApiResult.Error -> return@onEach
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.mainSearch.setOnSearchClickListener {
            lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                fetchRecentViewModel.fetchRecentSearch(token).collect {
                    when (it) {
                        is ApiResult.Success -> {
                            Toast.makeText(context, "최근 목적지 검색!", Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> return@collect
                    }
                }
            }
        }

        binding.testBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), PagingTestActivity::class.java))
        }

        setCurrentPositionText()

        binding.homeBtn.setOnClickListener { //id: 2
            val address = arguments?.getString("address") ?: return@setOnClickListener
            val lat = arguments?.getDouble("lat") ?: return@setOnClickListener
            val lng = arguments?.getDouble("lng") ?: return@setOnClickListener
            val location = Location(address = address, lon = lng.toString(), lat = lat.toString(), createdAt = System.currentTimeMillis(), name = "Home", number = 432)
            lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                saveDestinationViewModel.saveDestination(token, location).collect {
                    when (it) {
                        is ApiResult.Success -> {
                            Toast.makeText(context, "Home 위치 저장 완료!", Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> return@collect
                    }
                }

//                saveDestinationViewModel.fetchDestinationInfo(token, 2).collect {
//                    when (it) {
//                        is ApiResult.Success -> {
//                            Toast.makeText(context, "Home 위치 저장 완료!", Toast.LENGTH_SHORT).show()
//                        }
//                        is ApiResult.Error -> return@collect
//                    }
//                }
            }
        }
    }

    private fun setCurrentPositionText() = with(binding) {
        val current = arguments?.getString("address")
        if (current.isNullOrEmpty()) return@with
        currentLayout.visibility = View.VISIBLE
        currentPosText.text = current
    }
}