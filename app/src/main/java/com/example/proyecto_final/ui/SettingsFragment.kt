package com.example.proyecto_final.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("nodo_civico_prefs", Context.MODE_PRIVATE)

        view.findViewById<ImageButton>(R.id.btnBackAjustes).setOnClickListener {
            findNavController().navigateUp()
        }

        val spinnerTema = view.findViewById<AutoCompleteTextView>(R.id.spinnerTema)
        val spinnerNotif = view.findViewById<AutoCompleteTextView>(R.id.spinnerNotif)
        val spinnerFiltro = view.findViewById<AutoCompleteTextView>(R.id.spinnerFiltro)

        spinnerTema.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Sistema", "Claro", "Oscuro")))
        spinnerNotif.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Activas", "Silenciosas", "Desactivadas")))
        spinnerFiltro.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Todos", "Abiertos", "En proceso", "Cerrados")))

        spinnerTema.setText(prefs.getString("tema", "Sistema"), false)
        spinnerNotif.setText(prefs.getString("notif", "Activas"), false)
        spinnerFiltro.setText(prefs.getString("filtro", "Todos"), false)

        view.findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            val temaSeleccionado = spinnerTema.text.toString()

            prefs.edit()
                .putString("tema", temaSeleccionado)
                .putString("notif", spinnerNotif.text.toString())
                .putString("filtro", spinnerFiltro.text.toString())
                .apply()

            when (temaSeleccionado) {
                "Oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "Claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            Toast.makeText(requireContext(), "Configuración aplicada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
}