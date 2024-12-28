package com.escandan.firebasetestapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.model.DrivingDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrivingDetailAdapter(
    private var drivingDetailList: MutableList<DrivingDetail> // Mutable liste
) : RecyclerView.Adapter<DrivingDetailAdapter.DrivingDetailViewHolder>() {

    // ViewHolder sınıfı
    inner class DrivingDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.detailDate)
        private val nameTextView: TextView = itemView.findViewById(R.id.detailName)
        private val addressTextView: TextView = itemView.findViewById(R.id.detailAddress)
        private val tirednessCountTextView: TextView = itemView.findViewById(R.id.detailTirednessCount)

        private fun convertTimestampToDate(timestamp: Long): String {
            val date = Date(timestamp)
            val formated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            return formated.format(date)
        }

        // Verileri bağlama
        fun bind(drivingDetails: DrivingDetail) {
            val formatedDate = convertTimestampToDate(drivingDetails.date)
            dateTextView.text = formatedDate ?: "N/A" // Null durum kontrolü
            nameTextView.text = drivingDetails.stationName ?: "N/A"
            addressTextView.text = drivingDetails.stationAddress ?: "N/A"
            tirednessCountTextView.text = drivingDetails.tirednessCount?.toString() ?: "0"
        }
    }

    // ViewHolder oluşturma
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrivingDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driving_detail, parent, false)
        return DrivingDetailViewHolder(view)
    }

    // Listedeki eleman sayısı
    override fun getItemCount(): Int = drivingDetailList.size

    // ViewHolder ile verileri bağlama
    override fun onBindViewHolder(holder: DrivingDetailViewHolder, position: Int) {
        val detail = drivingDetailList[position]
        Log.d("DrivingDetailAdapter", "Binding data at position $position: $detail")
        holder.bind(detail)
    }

    // Verileri güncelleme yöntemi
    fun updateData(newData: List<DrivingDetail>) {
        Log.d("detailList", drivingDetailList.size.toString())
        drivingDetailList.clear()
        drivingDetailList.addAll(newData)
        notifyDataSetChanged() // Adapter'i verilerin değiştiği konusunda bilgilendir
    }
}
