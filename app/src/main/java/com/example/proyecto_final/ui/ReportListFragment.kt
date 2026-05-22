package com.example.proyecto_final.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.NodoCivicoApp
import com.example.proyecto_final.R
import com.example.proyecto_final.ui.adapter.ReportAdapter
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

class ReportListFragment :
    Fragment(R.layout.fragment_report_list) {

    private val viewModel: ReportViewModel by viewModels {

        AppViewModelFactory(
            (requireActivity().application as NodoCivicoApp).repository
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(
            R.id.btnBackLista
        ).setOnClickListener {

            findNavController().navigateUp()
        }

        val recyclerView =
            view.findViewById<RecyclerView>(
                R.id.recyclerViewReports
            )

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        val adapter =
            ReportAdapter(emptyList())

        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.allReports.collect {

                adapter.updateData(it)
            }
        }
    }
}