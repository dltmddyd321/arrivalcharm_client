package com.example.arrivalcharm.core

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object AppConst {
    const val NAVER_CLIENT_ID = "7SdnehddxOYovql_CXvA"
    const val NAVER_CLIENT_SECRET = "EgCKdBS7Vz"
    const val GOOGLE_CLIENT_ID = "151091018182-mb44kjv3pbgfiagqh9s18jva8ef17caf.apps.googleusercontent.com"

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}