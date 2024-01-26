package com.example.arrivalcharm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.arrivalcharm.R
import com.example.arrivalcharm.databinding.FragmentMapBinding
import com.example.arrivalcharm.util.DistanceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var map: GoogleMap? = null

    companion object {
        fun newInstance(lat: Double, lng: Double): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lng", lng)
            fragment.arguments = args
            return fragment
        }
    }

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val lat = arguments?.getDouble("lat", 37.4979) ?: 37.4979
        val lng = arguments?.getDouble("lng", 127.0276) ?: 127.0276

        val address = DistanceManager.getAddress(latitude = lat, longitude = lng, requireContext())

        val seoul = LatLng(lat, lng)

        val markerOptions = MarkerOptions()
        markerOptions.position(seoul)
        markerOptions.title("현재 위치")
        markerOptions.snippet(address)

        map?.addMarker(markerOptions)

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 17f))
    }
}