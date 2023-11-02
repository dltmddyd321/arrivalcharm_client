package com.example.arrivalcharm.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.arrivalcharm.R
import com.example.arrivalcharm.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var map: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }

    private fun onLocationChanged(location: Location) {
        mLastLocation = location

        val seoul = LatLng(location.latitude, location.longitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(seoul)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")

        map?.addMarker(markerOptions)

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))

    }

    private fun checkPermissionForLocation(): Boolean {
        return if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            // 권한이 없으므로 권한 요청 보내기
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_LOCATION)
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (checkPermissionForLocation()) startLocationUpdate()
    }

    override fun onResume() {
        super.onResume()
        if (map != null) startLocationUpdate()
    }
}