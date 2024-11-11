package com.bardaval.maplocation

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var editLocation: EditText
    private lateinit var textLat: TextView
    private lateinit var textLon: TextView

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Link UI components
        editLocation = findViewById(R.id.editTextText)
        textLat = findViewById(R.id.textView)
        textLon = findViewById(R.id.textView2)
        val btn: Button = findViewById(R.id.button)

        // Set click listener for the button
        btn.setOnClickListener {
            val location = editLocation.text.toString().trim()

            if (location.isEmpty()) {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            googleMap.clear()  // Clear any previous markers
            getLocationFromAddress(location)  // Fetch location and display
        }
    }

    // Callback for when the map is ready to be used
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    // Fetch location details from a given address string
    private fun getLocationFromAddress(location: String) {
        val geocoder = Geocoder(this)
        val addressList: List<Address>?

        try {
            // Get location information from the address
            addressList = geocoder.getFromLocationName(location, 5)

            if (addressList.isNullOrEmpty()) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                return
            }

            // Extract the latitude and longitude from the first result
            val address: Address = addressList[0]
            latitude = address.latitude
            longitude = address.longitude

            // Display the latitude and longitude in the text views
            textLat.text = "Latitude: $latitude"
            textLon.text = "Longitude: $longitude"

            // Add marker to the map
            val latLng = LatLng(latitude, longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Location: ${address.getAddressLine(0)}") // Display full address as title
            )
            // Move and zoom the camera to the found location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
        }
    }
}
