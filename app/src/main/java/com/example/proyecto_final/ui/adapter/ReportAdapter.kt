package com.example.proyecto_final.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.R
import com.example.proyecto_final.data.ReportEntity
import java.util.concurrent.TimeUnit
import android.widget.ImageView

class ReportAdapter(
    private var reports: List<ReportEntity>,
    private val onItemClick: (ReportEntity) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        val ivSyncIcon: ImageView =
            itemView.findViewById(R.id.ivSyncIcon)
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
        holder.tvTimestamp.text = getTimeAgo(report.timestamp)

        if (!report.isSynced) {
            holder.ivSyncIcon.visibility = View.VISIBLE
        } else {
            holder.ivSyncIcon.visibility = View.GONE
        }

        val statusColor = when (report.status) {
            "Abierto" -> R.color.status_text_open
            "En proceso" -> R.color.status_text_progress
            "Cerrado" -> R.color.status_text_closed
            else -> R.color.text_secondary_light
        }
        val bgColor = when (report.status) {
            "Abierto" -> R.color.status_bg_open
            "En proceso" -> R.color.status_bg_progress
            "Cerrado" -> R.color.status_bg_closed
            else -> R.color.surface_light
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

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Hace unos segundos"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Hace $minutes minuto${if (minutes > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "Hace $hours hora${if (hours > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "Hace $days día${if (days > 1) "s" else ""}"
            }
            else -> {
                val date = java.util.Date(timestamp)
                val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                format.format(date)
            }
        }
    }
}