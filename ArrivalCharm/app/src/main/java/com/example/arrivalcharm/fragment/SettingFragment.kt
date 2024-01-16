package com.example.arrivalcharm.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.arrivalcharm.activity.AlarmSettingActivity
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentSettingBinding
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.view.NameEditDialog
import com.example.arrivalcharm.viewmodel.ClearRecentViewModel
import com.example.arrivalcharm.viewmodel.DestinationsClearViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import com.example.arrivalcharm.viewmodel.UserUpdateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private var imageUri: Uri? = null

    @NetworkModule.Main
    private val userEditViewModel: UserUpdateViewModel by activityViewModels()

    @NetworkModule.Main
    private val tokenRefreshViewModel: TokenRefreshViewModel by activityViewModels()

    @NetworkModule.Main
    private val clearDestinationsViewModel: DestinationsClearViewModel by activityViewModels()

    @NetworkModule.Main
    private val clearRecentViewModel: ClearRecentViewModel by activityViewModels()

    private val dataStoreViewModel: DatastoreViewModel by activityViewModels()

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            }
        }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    imageUri = it
                    binding.profileImg.setImageURI(imageUri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImg.clipToOutline = true

        binding.profileEditLy.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        val imageUrl = "http://210.103.99.38:8080/image/default/profile.jpg"
        Glide.with(this)
            .load(imageUrl)
            .into(binding.profileImg)

        binding.nameEditLy.setOnClickListener {
            val dialog = NameEditDialog(requireContext(), "이름 변경", { newName ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val token = dataStoreViewModel.getAuthToken()
                    val refreshToken = dataStoreViewModel.getRefreshToken()
                    val userId = dataStoreViewModel.getAuthId()
                    userEditViewModel.updateUserName(token, userId, newName).collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                if (result.data.isSuccess) {
                                    Toast.makeText(
                                        context,
                                        "저장되었습니다. ${result.data.responseCode}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.settingOne.text = newName
                                }
                                if (result.data.responseCode == 500) {
                                    refreshToken(refreshToken, userId, newName)
                                }
                            }
                            else -> return@collect
                        }
                    }
                }
            }, {
                Toast.makeText(context, "변경이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            })
            dialog.show()
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

        lifecycleScope.launch {
            binding.userName.text = dataStoreViewModel.getUserName()
        }
    }

    private suspend fun refreshToken(refreshToken: String, userId: Int, updateName: String) {
        tokenRefreshViewModel.refreshToken(refreshToken).collect {
            when (it) {
                is ApiResult.Success -> {
                    val newRefreshToken = it.data?.refreshToken
                    val newAccessToken = it.data?.accessToken
                    if (!newRefreshToken.isNullOrEmpty()) dataStoreViewModel.putRefreshToken(
                        newRefreshToken
                    )
                    if (!newAccessToken.isNullOrEmpty()) dataStoreViewModel.putAuthToken(
                        newAccessToken
                    )
                    userEditViewModel.updateUserName(
                        dataStoreViewModel.getAuthToken(),
                        userId,
                        updateName
                    ).collect()
                }
                else -> {}
            }
        }
    }
}