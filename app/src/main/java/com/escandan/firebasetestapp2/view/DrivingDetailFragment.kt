package com.escandan.firebasetestapp2.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.adapter.DrivingDetailAdapter
import com.escandan.firebasetestapp2.databinding.FragmentDrivingDetailBinding
import com.escandan.firebasetestapp2.model.DrivingDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DrivingDetailFragment : Fragment() {
    private var _binding: FragmentDrivingDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var drivingDetailAdapter: DrivingDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrivingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ve Adapter kurulumları
        setupRecyclerView()
        fetchDataFirestore() // Firestore'dan veri çek
    }

    private fun setupRecyclerView() {
        // RecyclerView için adapter ve layoutManager ayarları
        drivingDetailAdapter = DrivingDetailAdapter(mutableListOf())
        binding.drivingDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()) // Dikey sıralama
            adapter = drivingDetailAdapter
        }
    }

    private fun fetchDataFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("FirestoreError", "User ID is null. Make sure the user is logged in.")
            return
        }

        // Firestore'dan verileri çek
        db.collection("Tiredness")
            .document(userId)
            .collection("StationDatas")
            .get()
            .addOnSuccessListener { result ->
                val details = mutableListOf<DrivingDetail>()
                for (document in result) {
                    try {
                        val data = document.toObject(DrivingDetail::class.java)
                        Log.d("Firestore.data", data.toString())
                        details.add(data)
                    } catch (e: Exception) {
                        Log.e("FirestoreError", "Error converting document to object", e)
                    }
                }
                drivingDetailAdapter.updateData(details) // Verileri güncelle
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching documents", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
