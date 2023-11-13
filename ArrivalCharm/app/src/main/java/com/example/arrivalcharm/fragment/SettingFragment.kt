package com.example.arrivalcharm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.arrivalcharm.R
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

   private lateinit var binding: FragmentSettingBinding

   @NetworkModule.Main


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}