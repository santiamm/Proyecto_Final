package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class ReportDetailFragment : Fragment(R.layout.fragment_report_detail) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnResolver).setOnClickListener {
            Toast.makeText(requireContext(), "Caso marcado como resuelto", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
}