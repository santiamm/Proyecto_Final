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
import com.example.proyecto_final.data.ReportEntity
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class EditReportFragment : Fragment(R.layout.fragment_edit_report) {

    private val viewModel: ReportViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as NodoCivicoApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reportId = arguments?.getInt("reportId") ?: 0
        val currentTitle = arguments?.getString("title") ?: ""
        val currentDesc = arguments?.getString("desc") ?: ""
        val currentCat = arguments?.getString("cat") ?: ""
        val currentPrio = arguments?.getString("prio") ?: ""
        val currentStatus = arguments?.getString("status") ?: "Abierto"

        view.findViewById<ImageButton>(R.id.btnBackEditar).setOnClickListener {
            findNavController().navigateUp()
        }

        val etTitulo = view.findViewById<TextInputEditText>(R.id.etTituloEditar)
        val etDescripcion = view.findViewById<TextInputEditText>(R.id.etDescripcionEditar)
        val spinnerCategoria = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerCategoriaEditar)
        val spinnerPrioridad = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerPrioridadEditar)
        val btnActualizar = view.findViewById<Button>(R.id.btnActualizarReporte)

        etTitulo.setText(currentTitle)
        etDescripcion.setText(currentDesc)
        spinnerCategoria.setText(currentCat, false)
        spinnerPrioridad.setText(currentPrio, false)

        val categorias = arrayOf("Alumbrado Público", "Vías e Infraestructura", "Seguridad Comunitaria", "Aseo Urbano")
        val prioridades = arrayOf("Baja", "Media", "Alta", "Crítica")

        spinnerCategoria.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias))
        spinnerPrioridad.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, prioridades))

        btnActualizar.setOnClickListener {
            val newTitle = etTitulo.text.toString().trim()
            val newDesc = etDescripcion.text.toString().trim()
            val newCat = spinnerCategoria.text.toString()
            val newPrio = spinnerPrioridad.text.toString()

            if (newTitle.isEmpty() || newDesc.isEmpty() || newCat.isEmpty() || newPrio.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedReport = ReportEntity(
                id = reportId,
                title = newTitle,
                description = newDesc,
                category = newCat,
                priority = newPrio,
                status = currentStatus,
                isSynced = false,
                timestamp = arguments?.getLong("timestamp") ?: System.currentTimeMillis()
            )

            viewModel.updateReport(updatedReport) {
                Toast.makeText(requireContext(), "Reporte actualizado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
}