package com.example.arrivalcharm.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

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
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
    }

//    @Inject
//    lateinit var workerFactory: HiltWorkerFactory
//
//    override val workManagerConfiguration = Configuration.Builder()
//        .setWorkerFactory(workerFactory)
//        .build()

    private inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Timber.tag("Lifecycle").d("Created: " + activity.localClassName)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d("Lifecycle", "Started: ${activity.localClassName}")
        }

        override fun onActivityResumed(activity: Activity) {
            Log.d("Lifecycle", "Resumed: ${activity.localClassName}")
        }

        override fun onActivityPaused(activity: Activity) {
            Log.d("Lifecycle", "Paused: ${activity.localClassName}")
        }

        override fun onActivityStopped(activity: Activity) {
            Log.d("Lifecycle", "Stopped: ${activity.localClassName}")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            Log.d("Lifecycle", "SaveInstanceState: ${activity.localClassName}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d("Lifecycle", "Destroyed: ${activity.localClassName}")
        }
    }
}