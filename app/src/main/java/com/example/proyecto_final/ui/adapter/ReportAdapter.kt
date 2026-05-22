package com.example.proyecto_final.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final.R
import com.example.proyecto_final.data.ReportEntity

class ReportAdapter(
    private var reports: List<ReportEntity>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvInitial: TextView =
            view.findViewById(R.id.tvInitial)

        val tvTitle: TextView =
            view.findViewById(R.id.tvTitle)

        val tvSubtitle: TextView =
            view.findViewById(R.id.tvSubtitle)

        val tvStatus: TextView =
            view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_report,
                parent,
                false
            )

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReportViewHolder,
        position: Int
    ) {

        val report = reports[position]

        holder.tvInitial.text =
            report.title.take(1).uppercase()

        holder.tvTitle.text =
            report.title

        holder.tvSubtitle.text =
            "Prioridad ${report.priority}"

        holder.tvStatus.text =
            report.status
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    fun updateData(
        newReports: List<ReportEntity>
    ) {

        reports = newReports

        notifyDataSetChanged()
    }
}