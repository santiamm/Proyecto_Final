package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R
import com.example.proyecto_final.utils.SessionManager

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val userEmail = sessionManager.getUserEmail()
        val userName = sessionManager.getUserName()

        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvInitial = view.findViewById<TextView>(R.id.tvInitial)
        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val btnGuardarNombre = view.findViewById<Button>(R.id.btnGuardarNombre)

        tvEmail.text = userEmail
        etNombre.setText(userName)
        updateInitial(userName)

        view.findViewById<ImageButton>(R.id.btnBackPerfil).setOnClickListener {
            findNavController().navigateUp()
        }

        btnGuardarNombre.setOnClickListener {
            val newName = etNombre.text.toString().trim()
            if (newName.isNotEmpty()) {
                sessionManager.saveUserName(newName)
                updateInitial(newName)
                Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnDesconectar).setOnClickListener {
            sessionManager.logout()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }

    private fun updateInitial(name: String) {
        val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
        view?.findViewById<TextView>(R.id.tvInitial)?.text = initial
    }
}