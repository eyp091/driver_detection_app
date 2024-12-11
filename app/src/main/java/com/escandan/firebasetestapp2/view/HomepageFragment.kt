package com.escandan.firebasetestapp2.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.databinding.FragmentHomepageBinding
import com.escandan.firebasetestapp2.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newDrivingButton.setOnClickListener { newDriving(it) }

        getAuthUser()
    }

    private fun getAuthUser() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("Users").document(it).get().addOnSuccessListener { document ->
                if (document != null) {
                    //belge tamamen dolu
                    val data = document.data
                    data?.let {
                        binding.textNameHomepage.text = it["name"] as? String
                        binding.textEmailHomepage.text = it["email"] as? String
                        binding.textDateHomepage.text = it["birthDate"] as? String
                    }
                }
            }
        }
    }

    private fun newDriving (view: View) {
        val action = HomepageFragmentDirections.actionHomepageFragmentToDrivingFragment()
        view.findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}