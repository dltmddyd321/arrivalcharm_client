package com.example.arrivalcharm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentHomeBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.AdviceViewModel
import com.example.arrivalcharm.viewmodel.FetchRecentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val dataStoreViewModel: DatastoreViewModel by activityViewModels()

    @NetworkModule.Advice
    private val adviceViewModel: AdviceViewModel by activityViewModels()

    @NetworkModule.Main
    private val fetchRecentViewModel: FetchRecentViewModel by activityViewModels()

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
    }
}