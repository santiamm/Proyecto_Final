package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
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
import android.widget.Button

class ReportListFragment : Fragment(R.layout.fragment_report_list) {

    private val viewModel: ReportViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackLista).setOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReports)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyState)
        val btnFiltroTodos = view.findViewById<Button>(R.id.btnFiltroTodos)
        val btnFiltroAbiertos = view.findViewById<Button>(R.id.btnFiltroAbiertos)
        val btnFiltroProceso = view.findViewById<Button>(R.id.btnFiltroProceso)
        val btnFiltroCerrados = view.findViewById<Button>(R.id.btnFiltroCerrados)

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
        btnFiltroTodos.setOnClickListener {
            viewModel.setFilter("Todos")
        }

        btnFiltroAbiertos.setOnClickListener {
            viewModel.setFilter("Abiertos")
        }

        btnFiltroProceso.setOnClickListener {
            viewModel.setFilter("En proceso")
        }

        btnFiltroCerrados.setOnClickListener {
            viewModel.setFilter("Cerrados")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReports.collect { reports ->
                    adapter.updateData(reports)
                    val empty = reports.isEmpty()
                    tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                    recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
                }
            }
        }
    }
}
