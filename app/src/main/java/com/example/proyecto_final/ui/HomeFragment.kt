package com.example.proyecto_final.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
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

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: ReportViewModel by viewModel()
    private lateinit var bannerNoInternet: View

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            requireActivity().runOnUiThread {
                if (!isAdded) return@runOnUiThread
                showInternetBanner(false)
                viewModel.syncReports { success, detail ->
                    val title = if (success) "Sincronización automática completada" else "Sincronización automática parcial"
                    Toast.makeText(requireContext(), "$title\n$detail", Toast.LENGTH_LONG).show()
                }
            }
        }
        override fun onLost(network: Network) {
            requireActivity().runOnUiThread {
                if (!isAdded) return@runOnUiThread
                showInternetBanner(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTotales = view.findViewById<TextView>(R.id.tvTotales)
        val tvPendientes = view.findViewById<TextView>(R.id.tvPendientes)
        val tvSincronizados = view.findViewById<TextView>(R.id.tvSincronizados)
        bannerNoInternet = view.findViewById(R.id.bannerNoInternet)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allReportsRaw.collect { reports ->
                    tvTotales.text = reports.size.toString()
                    tvPendientes.text = reports.count { !it.isSynced }.toString()
                    tvSincronizados.text = reports.count { it.isSynced }.toString()
                }
            }
        }

        view.findViewById<View>(R.id.cardHistorial).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reportListFragment)
        }
        view.findViewById<View>(R.id.cardReportar).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createReportFragment)
        }
        view.findViewById<View>(R.id.cardPerfil).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        view.findViewById<View>(R.id.cardConfig).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        view.findViewById<Button>(R.id.btnNuevoReporteHome).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createReportFragment)
        }
        view.findViewById<Button>(R.id.btnVerReportesHome).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reportListFragment)
        }
        view.findViewById<Button>(R.id.btnSyncHome).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_syncFragment)
        }
        view.findViewById<Button>(R.id.btnRemindersHome).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_calendarRemindersFragment)
        }

        checkInternetAndShowBanner()
        registerNetworkCallback()
    }

    private fun checkInternetAndShowBanner() {
        showInternetBanner(!isNetworkAvailable())
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(ConnectivityManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(network)
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val info = cm.activeNetworkInfo
            return info != null && info.isConnected
        }
    }

    private fun showInternetBanner(show: Boolean) {
        bannerNoInternet.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun registerNetworkCallback() {
        val cm = requireContext().getSystemService(ConnectivityManager::class.java)
        cm.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            val cm = requireContext().getSystemService(ConnectivityManager::class.java)
            cm.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) { }
    }
}
