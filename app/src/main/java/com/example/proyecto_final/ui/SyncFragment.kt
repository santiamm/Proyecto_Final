package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

        val tvSyncStatus = view.findViewById<TextView>(R.id.tvSyncStatus)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReports.collect { reports ->
                    val pendientes = reports.count { !it.isSynced }
                    tvSyncStatus.text = "Pendientes de sincronización: $pendientes reportes locales"
                }
            }
        }

        view.findViewById<Button>(R.id.btnSyncNow).setOnClickListener {
            tvSyncStatus.text = "Sincronizando..."
            viewModel.syncReports { success, detail ->
                if (success) {
                    Toast.makeText(requireContext(), "Sincronización completada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Sincronización parcial", Toast.LENGTH_SHORT).show()
                }
                tvSyncStatus.text = detail
            }
        }
    }
}
