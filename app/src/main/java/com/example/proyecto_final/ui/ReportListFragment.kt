package com.example.proyecto_final.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.R
import com.example.proyecto_final.ui.adapter.ReportAdapter
import com.example.proyecto_final.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportListFragment : Fragment(R.layout.fragment_report_list) {

    private val viewModel: ReportViewModel by viewModel()
    private val filterPrefsName = "nodo_civico_prefs"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackLista).setOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReports)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyState)
        val progressReports = view.findViewById<ProgressBar>(R.id.progressReports)
        val prefs = requireContext().getSharedPreferences(filterPrefsName, Context.MODE_PRIVATE)
        val filterButtons = mapOf(
            "Todos" to view.findViewById<Button>(R.id.btnFilterTodos),
            "Abiertos" to view.findViewById<Button>(R.id.btnFilterAbiertos),
            "En proceso" to view.findViewById<Button>(R.id.btnFilterProceso),
            "Cerrados" to view.findViewById<Button>(R.id.btnFilterCerrados)
        )

        fun applyFilter(filter: String) {
            prefs.edit().putString("filtro", filter).apply()
            viewModel.setFilter(filter)
        }

        fun updateFilterButtons(selected: String) {
            val selectedBg = ContextCompat.getColor(requireContext(), R.color.primary_blue)
            val normalBg = ContextCompat.getColor(requireContext(), R.color.surface_light)
            val selectedText = ContextCompat.getColor(requireContext(), android.R.color.white)
            val normalText = ContextCompat.getColor(requireContext(), R.color.primary_blue)
            filterButtons.forEach { (filter, button) ->
                val isSelected = filter == selected
                button.backgroundTintList = ColorStateList.valueOf(if (isSelected) selectedBg else normalBg)
                button.setTextColor(if (isSelected) selectedText else normalText)
            }
        }

        filterButtons.forEach { (filter, button) ->
            button.setOnClickListener { applyFilter(filter) }
        }
        applyFilter(prefs.getString("filtro", "Todos") ?: "Todos")

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ReportAdapter(emptyList()) { report ->
            val bundle = Bundle().apply {
                putInt("reportId", report.id)
                putString("title", report.title)
                putString("desc", report.description)
                putString("cat", report.category)
                putString("prio", report.priority)
                putString("status", report.status)
                putLong("timestamp", report.timestamp)
                putInt("remoteId", report.remoteId)
                putBoolean("isSynced", report.isSynced)
            }
            findNavController().navigate(R.id.action_reportListFragment_to_reportDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReports.collect { reports ->
                    progressReports.visibility = View.GONE
                    adapter.updateData(reports)
                    val empty = reports.isEmpty()
                    tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                    recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentFilter.collect { filter ->
                    updateFilterButtons(filter)
                }
            }
        }
    }
}
