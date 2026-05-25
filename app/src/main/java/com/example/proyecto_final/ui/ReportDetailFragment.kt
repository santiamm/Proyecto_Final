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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
        val timestamp = arguments?.getLong("timestamp") ?: System.currentTimeMillis()

        view.findViewById<TextView>(R.id.tvDetalleTitulo).text = currentTitle
        view.findViewById<TextView>(R.id.tvDetalleDesc).text = currentDesc
        view.findViewById<TextView>(R.id.tvDetalleTimestamp).text = getTimeAgo(timestamp)

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
                putLong("timestamp", timestamp)
            }
            findNavController().navigate(R.id.action_reportDetailFragment_to_editReportFragment, bundle)
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Reportado hace unos segundos"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Reportado hace $minutes minuto${if (minutes > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "Reportado hace $hours hora${if (hours > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "Reportado hace $days día${if (days > 1) "s" else ""}"
            }
            else -> {
                val date = Date(timestamp)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                "Reportado el ${format.format(date)}"
            }
        }
    }
}