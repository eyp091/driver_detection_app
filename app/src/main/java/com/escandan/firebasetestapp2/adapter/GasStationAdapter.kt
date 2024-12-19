package com.escandan.firebasetestapp2.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.model.GasStation
import com.google.android.gms.maps.model.LatLng

class GasStationAdapter(
    //private val gasStations: List<GasStation>,
    //private val onItemClick: (GasStation) -> Unit
    private val context: Context,
    private val gasStations: List<GasStation>,
    private val userLocation: LatLng
) : RecyclerView.Adapter<GasStationAdapter.GasStationViewHolder>() {

    inner class GasStationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val nameTextView: TextView = itemView.findViewById(R.id.stationName)
        private val addressTextView: TextView = itemView.findViewById(R.id.stationAddress)

        fun bind(gasStation: GasStation) {
            nameTextView.text = gasStation.name
            addressTextView.text = gasStation.address

            itemView.setOnClickListener {
                val destinationLat = gasStation.latitude
                val destinationLng = gasStation.longitude

                val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${userLocation.latitude},${userLocation.longitude}&destination=$destinationLat,$destinationLng")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            /*
            itemView.setOnClickListener {
                onItemClick(gasStation)
            }
             */
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GasStationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gas_station, parent, false)
        return GasStationViewHolder(view)
    }

    override fun getItemCount(): Int = gasStations.size

    override fun onBindViewHolder(holder: GasStationViewHolder, position: Int) {
        val gasStation = gasStations[position]
        holder.bind(gasStation)
    }

}