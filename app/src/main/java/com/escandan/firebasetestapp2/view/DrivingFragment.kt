package com.escandan.firebasetestapp2.view

import android.Manifest
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.escandan.firebasetestapp2.databinding.FragmentDrivingBinding
import com.escandan.firebasetestapp2.util.SocketioManager
import com.google.android.gms.maps.model.LatLng
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.adapter.GasStationAdapter
import com.escandan.firebasetestapp2.model.GasStation
import com.escandan.firebasetestapp2.util.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class DrivingFragment : Fragment() {
    private var _binding : FragmentDrivingBinding? = null
    private val binding get() = _binding!!
    private lateinit var socketioManager: SocketioManager
    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null
    private lateinit var locationService: LocationService
    private lateinit var gasStationAdapter: GasStationAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrivingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //recycler view'ı tanımla
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Konum servisi aktif mi kontrol et
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            // Kullanıcıyı GPS'i açmaya teşvik et
            Toast.makeText(requireContext(), "GPS kapalı. Lütfen açın.", Toast.LENGTH_SHORT).show()
        }

        // Kullanıcıdan konum izni alma
        registerLauncher()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(requireView(), "Konum bilgilerine erişmek için izin veriyor musunuz?", Snackbar.LENGTH_INDEFINITE).setAction(
                    "İzin ver!"
                ) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null) {
                val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                Log.d("location.service", sonBilinenLatLng.toString())
            }

            // locationListener'ı burada başlatıyoruz
            if (locationListener == null) {
                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val kullaniciKonumu = LatLng(location.latitude, location.longitude)
                        Log.d("location.service", kullaniciKonumu.toString())

                        // Benzin istasyonlarını arama
                        locationService = LocationService(requireContext())
                        locationService.searchNearbyGasStation(
                            userLocation = kullaniciKonumu,
                            onResult = { stations ->
                                requireActivity().runOnUiThread {
                                    Log.d("location.service", "station: $stations")
                                    gasStationAdapter = GasStationAdapter(requireContext(), stations, kullaniciKonumu)
                                    recyclerView.adapter = gasStationAdapter
                                }
                            },
                            onError = { error ->
                                requireActivity().runOnUiThread {
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    }
                }
            }

        }

        // socketioManager ve bağlantı işlemleri
        socketioManager = SocketioManager { message ->
            requireActivity().runOnUiThread {
                if (message == "normal") {
                    binding.drivingNotificationResultText.text = message
                    binding.drivingTirednessState.isVisible = false
                } else if (message == "yorgun") {
                    binding.drivingNotificationResultText.text = message
                    binding.drivingNotificationResultText.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100f, locationListener!!)
                    binding.drivingTirednessState.isVisible = true
                } else {
                    binding.drivingNotificationResultText.text = message
                }
            }
        }
        socketioManager.connectToServer()
    }


    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if (result) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100f, locationListener!!)
                    val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (sonBilinenKonum != null) {
                        val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                        Log.d("location.service", sonBilinenLatLng.toString())
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "İzne ihtiyacımız var!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        socketioManager.disconnected()
    }

}