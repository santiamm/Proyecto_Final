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
import com.example.proyecto_final.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackAjustes).setOnClickListener {
            findNavController().navigateUp()
        }

        val spinnerTema = view.findViewById<AutoCompleteTextView>(R.id.spinnerTema)
        val spinnerNotif = view.findViewById<AutoCompleteTextView>(R.id.spinnerNotif)
        val spinnerFiltro = view.findViewById<AutoCompleteTextView>(R.id.spinnerFiltro)

        spinnerTema.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Sistema", "Claro", "Oscuro")))
        spinnerNotif.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Activas", "Silenciosas", "Desactivadas")))
        spinnerFiltro.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Todos", "Abiertos", "En proceso", "Cerrados")))

        view.findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            Toast.makeText(requireContext(), "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
}