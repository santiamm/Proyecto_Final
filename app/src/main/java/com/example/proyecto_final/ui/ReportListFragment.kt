package com.example.proyecto_final.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.NodoCivicoApp
import com.example.proyecto_final.R
import com.example.proyecto_final.ui.adapter.ReportAdapter
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

class ReportListFragment : Fragment(R.layout.fragment_report_list) {

    private val viewModel: ReportViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as NodoCivicoApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackLista).setOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReports)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyState)
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
            }
            findNavController().navigate(R.id.action_reportListFragment_to_reportDetailFragment, bundle)
        }

        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReports.collect { reports ->
                    val prefs = requireContext().getSharedPreferences("nodo_civico_prefs", Context.MODE_PRIVATE)
                    val filtroActual = prefs.getString("filtro", "Todos") ?: "Todos"

                    val reportesFiltrados = when (filtroActual) {
                        "Abiertos" -> reports.filter { it.status == "Abierto" }
                        "En proceso" -> reports.filter { it.status == "En proceso" }
                        "Cerrados" -> reports.filter { it.status == "Cerrado" }
                        else -> reports
                    }

                    adapter.updateData(reportesFiltrados)

                    if (tvEmpty != null) {
                        tvEmpty.visibility = if (reportesFiltrados.isEmpty()) View.VISIBLE else View.GONE
                        recyclerView.visibility = if (reportesFiltrados.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }
}
