package com.example.arrivalcharm.util

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class RequestPermissionUtil(private val context: Context) {

    companion object {
        val REQUEST_LOCATION = 180

        @RequiresApi(Build.VERSION_CODES.Q)
        val permissionsLocationUpApi29Impl = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        @TargetApi(Build.VERSION_CODES.P)
        val permissionsLocationDownApi29Impl = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}