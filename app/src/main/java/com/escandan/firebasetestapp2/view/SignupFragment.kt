package com.escandan.firebasetestapp2.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.escandan.firebasetestapp2.R
import com.escandan.firebasetestapp2.databinding.FragmentLoginBinding
import com.escandan.firebasetestapp2.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
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
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupSignupButton.setOnClickListener { signup(it) }
        binding.signupLoginButton.setOnClickListener { goToLoginPage(it) }
    }

    fun signup(view: View) {
        val email = binding.signupMailText.text.toString()
        val password = binding.signupPasswordText.text.toString()
        val name = binding.nameText.text.toString()
        val date = binding.dateText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && date.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // verileri veritabanına kaydedebiliriz.
                    val userId = auth.currentUser?.uid
                    val userMap = hashMapOf<String, Any>()
                    userMap.put("email", email)
                    userMap.put("name", name)
                    userMap.put("birthDate", date)

                    if (userId != null) {
                        db.collection("Users").document(userId).set(userMap).addOnSuccessListener { documentReference ->
                            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
                            view.findNavController().navigate(action)
                        }.addOnFailureListener { exception ->
                            println(exception)
                            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }



                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen zorunlu alanları doldurunuz!", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToLoginPage(view: View) {
        val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
        view.findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}