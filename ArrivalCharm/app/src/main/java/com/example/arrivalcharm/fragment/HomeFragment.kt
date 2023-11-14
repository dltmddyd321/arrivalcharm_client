package com.example.arrivalcharm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentHomeBinding
import com.example.arrivalcharm.viewmodel.AdviceViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    @NetworkModule.Advice
    private val adviceViewModel: AdviceViewModel by activityViewModels()

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


    }
}