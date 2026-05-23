package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.NodoCivicoApp
import com.example.proyecto_final.R
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private val viewModel: ReportViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as NodoCivicoApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackCrear).setOnClickListener {
            findNavController().navigateUp()
        }

        val etTitulo = view.findViewById<TextInputEditText>(R.id.etTitulo)
        val etDescripcion = view.findViewById<TextInputEditText>(R.id.etDescripcion)
        val spinnerCategoria = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerCategoria)
        val spinnerPrioridad = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerPrioridad)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarReporte)

        spinnerCategoria.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Alumbrado Público", "Vías e Infraestructura", "Seguridad Comunitaria", "Aseo Urbano"))
        )

        spinnerPrioridad.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Baja", "Media", "Alta", "Crítica"))
        )

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val desc = etDescripcion.text.toString().trim()
            val cat = spinnerCategoria.text.toString()
            val prio = spinnerPrioridad.text.toString()

            if (titulo.isEmpty() || desc.isEmpty() || cat.isEmpty() || prio.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveReport(titulo, desc, cat, prio) {
                    Toast.makeText(requireContext(), "Reporte guardado localmente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }
}