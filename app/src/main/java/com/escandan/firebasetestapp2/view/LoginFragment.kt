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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener { login(it) }
        binding.signupButton.setOnClickListener { signup(it) }

        val guncelKullanıcı = auth.currentUser
        if (guncelKullanıcı != null) {
            //kullanıcı giriş yapmış
        }
    }

    fun signup(view: View) {
        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        view.findNavController().navigate(action)
    }

    fun login(view: View) {

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { task ->
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                val action = LoginFragmentDirections.actionLoginFragmentToHomepageFragment()
                view.findNavController().navigate(action)
            }.addOnFailureListener {exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}