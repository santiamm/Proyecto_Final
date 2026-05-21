package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class ReportListFragment : Fragment(R.layout.fragment_report_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackLista).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<CardView>(R.id.cardReporteEjemplo).setOnClickListener {
            findNavController().navigate(R.id.action_reportListFragment_to_reportDetailFragment)
        }
    }
}