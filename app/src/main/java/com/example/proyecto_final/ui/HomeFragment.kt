package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CardView>(R.id.cardLista).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reportListFragment)
        }

        view.findViewById<CardView>(R.id.cardCrear).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createReportFragment)
        }

        view.findViewById<CardView>(R.id.cardAjustes).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        view.findViewById<CardView>(R.id.cardPerfil).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }
}