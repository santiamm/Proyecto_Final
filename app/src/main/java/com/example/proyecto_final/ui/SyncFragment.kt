package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R
import com.example.proyecto_final.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SyncFragment : Fragment(R.layout.fragment_sync) {

    private val viewModel: ReportViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBackSync).setOnClickListener {
            findNavController().navigateUp()
        }

        val tvSyncPending = view.findViewById<TextView>(R.id.tvSyncPending)
        val tvSyncStatus = view.findViewById<TextView>(R.id.tvSyncStatus)
        val progressSync = view.findViewById<View>(R.id.progressSync)
        val btnSyncNow = view.findViewById<Button>(R.id.btnSyncNow)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReportsRaw.collect { reports ->
                    val pendientes = reports.count { !it.isSynced }
                    tvSyncPending.text = "Pendientes: $pendientes"
                }
            }
        }

        btnSyncNow.setOnClickListener {
            tvSyncStatus.text = "Sincronizando con el servidor..."
            progressSync.visibility = View.VISIBLE
            btnSyncNow.isEnabled = false
            
            viewModel.syncReports { success, detail ->
                progressSync.visibility = View.GONE
                btnSyncNow.isEnabled = true
                if (success) {
                    tvSyncStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))
                    Toast.makeText(requireContext(), "Sincronización completada", Toast.LENGTH_SHORT).show()
                } else {
                    tvSyncStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_orange))
                    Toast.makeText(requireContext(), "Sincronización con errores", Toast.LENGTH_SHORT).show()
                }
                tvSyncStatus.text = detail
            }
        }
    }
}
