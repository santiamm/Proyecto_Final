package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
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

        view.findViewById<TextView>(R.id.tvEmail).text = userEmail
        view.findViewById<TextView>(R.id.tvInitial).text = userEmail.take(1).uppercase()

        view.findViewById<ImageButton>(R.id.btnBackPerfil).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btnDesconectar).setOnClickListener {
            sessionManager.logout()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }
}