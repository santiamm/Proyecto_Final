package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnCrear).setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_createReportFragment) }
        view.findViewById<Button>(R.id.btnLista).setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_reportListFragment) }
        view.findViewById<Button>(R.id.btnPerfil).setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
        view.findViewById<Button>(R.id.btnAjustes).setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_settingsFragment) }
    }
}