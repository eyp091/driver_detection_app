package com.escandan.firebasetestapp2.util

import android.content.Context
import android.util.Log
import com.escandan.firebasetestapp2.model.GasStation
import com.google.android.gms.maps.model.LatLng
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LocationService(private val context: Context) {

    private val API_KEY = "AIzaSyDniXprObQN-9TlnwE73P5GgtFplAqOTP4"
    private val client = OkHttpClient()

    //kullanıcının konumunu parametre olarak alıp arama yapmak
    fun searchNearbyGasStation(
        userLocation: LatLng,
        onResult: (List<GasStation>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=${userLocation.latitude},${userLocation.longitude}" +
                "&radius=5000" + // 5000 metre içinde ara
                "&type=gas_station" +
                "&key=$API_KEY"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NearybySearch", "Request failed: ${e.message}")
                onError("Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val gasStation = parseNearbyPlaces(responseBody)
                        onResult(gasStation)
                    } else {
                        onError("Response body is null!")
                    }
                } else {
                    onError("Request not successful: ${response.code}")
                }
            }

        })
    }

    //API'den gelen json yanıtlarını işler
    private fun parseNearbyPlaces(responseBody: String): List<GasStation> {
        val json = JSONObject(responseBody)
        val result = json.getJSONArray("results")

        val gasStations = mutableListOf<GasStation>()
        for (i in 0 until Math.min(5, result.length())) {
            val place = result.getJSONObject(i)
            val name = place.getString("name")
            val vicinity = place.getString("vicinity")

            //konum bilgileri
            val geometry = place.getJSONObject("geometry")
            val location = geometry.getJSONObject("location")
            val latitude = location.getDouble("lat")
            val longitude = location.getDouble("lng")

            //gasStations.add("$name - $vicinity - $latitude - $longitude")
            val gasStation = GasStation(name, vicinity, latitude, longitude)
            gasStations.add(gasStation)
        }

        return gasStations
    }

}