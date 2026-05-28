package com.example.proyecto_final.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R
import com.example.proyecto_final.utils.ReminderManager
import com.example.proyecto_final.viewmodel.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class ReportDetailFragment : Fragment(R.layout.fragment_report_detail) {

    private val viewModel: ReportViewModel by viewModel()

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
        val remoteId = arguments?.getInt("remoteId") ?: 0
        val isSynced = arguments?.getBoolean("isSynced") ?: false

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
                findNavController().navigateUp()
            }
        }

        view.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            if (reportId != 0) {
                viewModel.deleteReport(reportId)
                Toast.makeText(requireContext(), "Reporte eliminado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
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
                putInt("remoteId", remoteId)
                putBoolean("isSynced", isSynced)
            }
            findNavController().navigate(R.id.action_reportDetailFragment_to_editReportFragment, bundle)
        }

        view.findViewById<Button>(R.id.btnRecordatorio).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
                    Toast.makeText(requireContext(), "Concede permiso de notificaciones", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            mostrarSelectorFechaHoraSeguro(reportId, currentTitle)
        }
    }

    private fun mostrarSelectorFechaHoraSeguro(reportId: Int, reportTitle: String) {
        if (!isAdded || context == null) return
        val calendar = Calendar.getInstance()
        try {
            val datePicker = DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hourOfDay, minute ->
                            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedDate.set(Calendar.MINUTE, minute)
                            val triggerTime = selectedDate.timeInMillis
                            if (triggerTime > System.currentTimeMillis()) {
                                try {
                                    val reminderManager = ReminderManager(requireContext())
                                    reminderManager.scheduleReminder(reportId, reportTitle, triggerTime - System.currentTimeMillis())
                                    val fechaFormateada = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", selectedDate)
                                    Toast.makeText(requireContext(), "Recordatorio programado para $fechaFormateada", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "Error al programar: ${e.message}", Toast.LENGTH_LONG).show()
                                    e.printStackTrace()
                                }
                            } else {
                                Toast.makeText(requireContext(), "La fecha debe ser futura", Toast.LENGTH_SHORT).show()
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al abrir selector: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60000 -> "Reportado hace unos segundos"
            diff < 3600000 -> {
                val minutes = diff / 60000
                "Reportado hace $minutes minuto${if (minutes > 1) "s" else ""}"
            }
            diff < 86400000 -> {
                val hours = diff / 3600000
                "Reportado hace $hours hora${if (hours > 1) "s" else ""}"
            }
            diff < 604800000 -> {
                val days = diff / 86400000
                "Reportado hace $days día${if (days > 1) "s" else ""}"
            }
            else -> {
                val date = java.util.Date(timestamp)
                val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                "Reportado el ${format.format(date)}"
            }
        }
    }
}
