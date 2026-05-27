package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R
import com.example.proyecto_final.utils.SessionManager
import com.example.proyecto_final.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        if (sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)

        view.findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(email, pass)
        }

        view.findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.register(email, pass)
        }

        lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                when (result) {
                    true -> {
                        sessionManager.saveLoginSession(etEmail.text.toString().trim())
                        viewModel.resetAuthResult()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    false -> {
                        Toast.makeText(requireContext(), "Credenciales inválidas o usuario ya existe", Toast.LENGTH_SHORT).show()
                        viewModel.resetAuthResult()
                    }
                    null -> {}
                }
            }
        }
    }
}