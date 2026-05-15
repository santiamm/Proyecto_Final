package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_final.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnSincronizar).setOnClickListener {
            Toast.makeText(requireContext(), "Datos sincronizados", Toast.LENGTH_SHORT).show()
        }
    }
}