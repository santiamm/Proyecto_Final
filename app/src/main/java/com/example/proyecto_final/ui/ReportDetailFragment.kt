package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.NodoCivicoApp
import com.example.proyecto_final.R
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel

class ReportDetailFragment : Fragment(R.layout.fragment_report_detail) {

    private val viewModel: ReportViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as NodoCivicoApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackDetalle).setOnClickListener {
            findNavController().navigateUp()
        }

        val reportId = arguments?.getInt("reportId") ?: 0
        val currentTitle = arguments?.getString("title") ?: "Sin título"
        val currentDesc = arguments?.getString("desc") ?: "Sin descripción"
        val currentCat = arguments?.getString("cat") ?: ""
        val currentPrio = arguments?.getString("prio") ?: ""
        val currentStatus = arguments?.getString("status") ?: "Abierto"

        view.findViewById<TextView>(R.id.tvDetalleTitulo).text = currentTitle
        view.findViewById<TextView>(R.id.tvDetalleDesc).text = currentDesc

        view.findViewById<Button>(R.id.btnEnProceso).setOnClickListener {
            if (reportId != 0) {
                viewModel.markAsInProgress(reportId)
                Toast.makeText(requireContext(), "Reporte marcado como En proceso", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        view.findViewById<Button>(R.id.btnResolver).setOnClickListener {
            if (reportId != 0) {
                viewModel.markAsResolved(reportId)
                Toast.makeText(requireContext(), "Reporte cerrado", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            if (reportId != 0) {
                viewModel.deleteReport(reportId)
                Toast.makeText(requireContext(), "Reporte eliminado", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btnEditar).setOnClickListener {
            val bundle = Bundle().apply {
                putInt("reportId", reportId)
                putString("title", currentTitle)
                putString("desc", currentDesc)
                putString("cat", currentCat)
                putString("prio", currentPrio)
                putString("status", currentStatus)
            }
            findNavController().navigate(R.id.action_reportDetailFragment_to_editReportFragment, bundle)
        }
    }
}