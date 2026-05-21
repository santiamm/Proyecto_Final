package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackPerfil).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btnDesconectar).setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}