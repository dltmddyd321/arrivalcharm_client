package com.example.arrivalcharm.util

import android.Manifest

class PermissionUtil {

    companion object {
        const val REQUEST_LOCATION = 1124
    }

    private val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun getPermissions(): Array<String> {
        return permissionsLocation
    }
}