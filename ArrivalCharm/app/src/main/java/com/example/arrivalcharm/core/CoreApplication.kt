package com.example.arrivalcharm.core

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "dd2a2a9fa7c29f1a6da2e928efbaee85")
        NaverIdLoginSDK.initialize(
            this,
            AppConst.NAVER_CLIENT_ID,
            AppConst.NAVER_CLIENT_SECRET,
            "ArrivalCharm"
        )
    }
}