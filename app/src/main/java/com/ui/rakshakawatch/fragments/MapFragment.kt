package com.ui.rakshakawatch.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize map configuration
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))

        map = view.findViewById(R.id.map)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)

        // Compass overlay
        val compassOverlay = CompassOverlay(context, map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)

        // Initialize and configure location overlay
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)

        // Refresh button to update location
        val refreshButton = view.findViewById<FloatingActionButton>(R.id.btn_refresh)
        refreshButton.setOnClickListener {
            refreshLocation()
        }

        return view
    }

    private fun refreshLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = locationOverlay.myLocation
            if (location != null) {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                map.controller.animateTo(geoPoint)
                map.overlays.clear()

                // Add a marker for the current location
                val marker = Marker(map)
                marker.position = geoPoint
                marker.title = "You are here"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(marker)

                Toast.makeText(requireContext(), "Location refreshed!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Unable to get current location!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Location permission not granted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
