package com.example.proyecto_final.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final.R
import com.example.proyecto_final.utils.ReminderManager
import com.example.proyecto_final.utils.ScheduledReminder
import java.util.Calendar

class CalendarRemindersFragment : Fragment(R.layout.fragment_calendar_reminders) {

    private lateinit var reminderManager: ReminderManager
    private lateinit var remindersContainer: LinearLayout
    private lateinit var tvEmptyReminders: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reminderManager = ReminderManager(requireContext())
        remindersContainer = view.findViewById(R.id.remindersContainer)
        tvEmptyReminders = view.findViewById(R.id.tvEmptyReminders)

        view.findViewById<ImageButton>(R.id.btnBackReminders).setOnClickListener {
            findNavController().navigateUp()
        }

        renderReminders()
    }

    private fun renderReminders() {
        remindersContainer.removeAllViews()
        val reminders = reminderManager.getScheduledReminders()
        tvEmptyReminders.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE

        reminders.forEach { reminder ->
            remindersContainer.addView(createReminderCard(reminder))
        }
    }

    private fun createReminderCard(reminder: ScheduledReminder): View {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_reminder, remindersContainer, false)
        
        view.findViewById<TextView>(R.id.tvReminderTitle).text = reminder.title
        view.findViewById<TextView>(R.id.tvReminderDate).text = "Programado: ${DateFormat.format("dd/MM/yyyy HH:mm", reminder.triggerTime)}"
        
        view.findViewById<Button>(R.id.btnEditReminder).setOnClickListener {
            showRescheduleDialog(reminder)
        }
        
        view.findViewById<Button>(R.id.btnCancelReminder).setOnClickListener {
            reminderManager.cancelReminder(reminder.reportId)
            Toast.makeText(requireContext(), "Recordatorio cancelado", Toast.LENGTH_SHORT).show()
            renderReminders()
        }
        
        return view
    }

    private fun showRescheduleDialog(reminder: ScheduledReminder) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                TimePickerDialog(
                    requireContext(),
                    R.style.CustomTimePickerDialog,
                    { _, hourOfDay, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDate.set(Calendar.MINUTE, minute)
                        selectedDate.set(Calendar.SECOND, 0)
                        val delay = selectedDate.timeInMillis - System.currentTimeMillis()
                        if (delay > 0) {
                            reminderManager.scheduleReminder(reminder.reportId, reminder.title, delay)
                            Toast.makeText(requireContext(), "Recordatorio actualizado", Toast.LENGTH_SHORT).show()
                            renderReminders()
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
        ).show()
    }
}
