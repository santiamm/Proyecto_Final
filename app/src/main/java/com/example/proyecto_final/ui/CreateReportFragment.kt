package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.example.proyecto_final.R

class CreateReportFragment : Fragment(R.layout.fragment_create_report) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackCrear).setOnClickListener {
            findNavController().navigateUp()
        }

        val etTitulo = view.findViewById<TextInputEditText>(R.id.etTitulo)
        val etDescripcion = view.findViewById<TextInputEditText>(R.id.etDescripcion)
        val spinnerCategoria = view.findViewById<AutoCompleteTextView>(R.id.spinnerCategoria)
        val spinnerPrioridad = view.findViewById<AutoCompleteTextView>(R.id.spinnerPrioridad)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarReporte)

        val categorias = arrayOf("Alumbrado Público", "Vías e Infraestructura", "Seguridad Comunitaria", "Aseo Urbano")
        val prioridades = arrayOf("Baja", "Media", "Alta", "Crítica")

        spinnerCategoria.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias))
        spinnerPrioridad.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, prioridades))

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val desc = etDescripcion.text.toString().trim()
            if (titulo.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Reporte guardado localmente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
}