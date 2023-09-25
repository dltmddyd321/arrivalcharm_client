package com.example.arrivalcharm.core

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "dd2a2a9fa7c29f1a6da2e928efbaee85")
    }
}