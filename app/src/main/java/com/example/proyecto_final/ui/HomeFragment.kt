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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.NodoCivicoApp
import com.example.proyecto_final.R
import com.example.proyecto_final.viewmodel.AppViewModelFactory
import com.example.proyecto_final.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: ReportViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as NodoCivicoApp).repository)
    }

    private lateinit var bannerNoInternet: View

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            requireActivity().runOnUiThread { showInternetBanner(false) }
        }

        override fun onLost(network: Network) {
            requireActivity().runOnUiThread { showInternetBanner(true) }
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
                viewModel.allReports.collect { reports ->
                    tvTotales.text = reports.size.toString()
                    tvPendientes.text = reports.count { it.status == "Abierto" || it.status == "En proceso" }.toString()
                    tvSincronizados.text = reports.count { it.status == "Cerrado" }.toString()
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

        checkInternetAndShowBanner()
        registerNetworkCallback()
    }

    private fun checkInternetAndShowBanner() {
        val isConnected = isNetworkAvailable()
        showInternetBanner(!isConnected)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showInternetBanner(show: Boolean) {
        bannerNoInternet.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun registerNetworkCallback() {
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) { }
    }
}