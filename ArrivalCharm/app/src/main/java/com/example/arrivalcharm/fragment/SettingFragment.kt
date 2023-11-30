package com.example.arrivalcharm.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.arrivalcharm.activity.AlarmSettingActivity
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentSettingBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.ClearRecentViewModel
import com.example.arrivalcharm.viewmodel.DestinationsClearViewModel
import com.example.arrivalcharm.viewmodel.UserUpdateViewModel
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    @NetworkModule.Main
    private val userEditViewModel: UserUpdateViewModel by activityViewModels()

    @NetworkModule.Main
    private val clearDestinationsViewModel: DestinationsClearViewModel by activityViewModels()

    @NetworkModule.Main
    private val clearRecentViewModel: ClearRecentViewModel by activityViewModels()

    private val dataStoreViewModel: DatastoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = "http://210.103.99.38:8080/image/default/profile.jpg"
        Glide.with(this)
            .load(imageUrl)
            .into(binding.profileImg)

        binding.nameEditLy.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                val userId = dataStoreViewModel.getAuthId()
                val updateName = "승용차"
                userEditViewModel.updateUserName(token, userId, updateName).collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                            binding.settingOne.text = updateName
                        }
                        else -> return@collect
                    }
                }
            }
        }

        binding.initDestinationLy.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                clearDestinationsViewModel.clearDestinationList(token).collect {
                    when (it) {
                        is ApiResult.Success -> {
                            Toast.makeText(context, "초기화 완료.", Toast.LENGTH_SHORT).show()
                        }
                        else -> return@collect
                    }
                }
            }
        }

        binding.initRecentLy.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val token = dataStoreViewModel.getAuthToken()
                clearRecentViewModel.clearRecentList(token).collect {
                    when (it) {
                        is ApiResult.Success -> {
                            Toast.makeText(context, "초기화 완료.", Toast.LENGTH_SHORT).show()
                        }
                        else -> return@collect
                    }
                }
            }
        }

        binding.alarmEditLy.setOnClickListener {
            startActivity(Intent(requireContext(), AlarmSettingActivity::class.java))
        }
    }
}