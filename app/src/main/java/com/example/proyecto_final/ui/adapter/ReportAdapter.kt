package com.example.proyecto_final.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.R
import com.example.proyecto_final.data.ReportEntity

class ReportAdapter(
    private var reports: List<ReportEntity>,
    private val onItemClick: (ReportEntity) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.tvTitle.text = report.title
        holder.tvSubtitle.text = "${report.category} · ${report.priority}"
        holder.tvStatus.text = report.status

        val statusColor = when (report.status) {
            "Abierto" -> R.color.primary_blue
            "En proceso" -> R.color.warning_orange
            "Cerrado" -> R.color.success_green
            else -> R.color.muted_gray
        }
        val bgColor = when (report.status) {
            "Abierto" -> R.color.primary_blue_light
            "En proceso" -> R.color.warning_orange_light
            "Cerrado" -> R.color.success_green_light
            else -> R.color.muted_gray_light
        }
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, statusColor))
        holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, bgColor))

        holder.tvInitial.text = report.title.take(1).uppercase()
        holder.itemView.setOnClickListener { onItemClick(report) }
    }

    override fun getItemCount(): Int = reports.size

    fun updateData(newReports: List<ReportEntity>) {
        reports = newReports
        notifyDataSetChanged()
    }
}