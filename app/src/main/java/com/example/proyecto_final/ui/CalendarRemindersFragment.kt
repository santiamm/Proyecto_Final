package com.example.proyecto_final.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
        val card = CardView(requireContext()).apply {
            radius = resources.getDimension(R.dimen.card_reminder_radius)
            cardElevation = resources.getDimension(R.dimen.card_reminder_elevation)
            setCardBackgroundColor(requireContext().getColor(R.color.surface_light))
            useCompatPadding = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }

        val content = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val title = TextView(requireContext()).apply {
            text = reminder.title
            setTextColor(requireContext().getColor(R.color.text_primary_light))
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val date = TextView(requireContext()).apply {
            text = "Programado: ${DateFormat.format("dd/MM/yyyy HH:mm", reminder.triggerTime)}"
            setTextColor(requireContext().getColor(R.color.text_secondary_light))
            textSize = 14f
            setPadding(0, dp(6), 0, dp(12))
        }

        val actions = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val edit = Button(requireContext()).apply {
            text = "Editar"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = dp(6)
            }
            setOnClickListener { showRescheduleDialog(reminder) }
        }

        val cancel = Button(requireContext()).apply {
            text = "Cancelar"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = dp(6)
            }
            setOnClickListener {
                reminderManager.cancelReminder(reminder.reportId)
                Toast.makeText(requireContext(), "Recordatorio cancelado", Toast.LENGTH_SHORT).show()
                renderReminders()
            }
        }

        actions.addView(edit)
        actions.addView(cancel)
        content.addView(title)
        content.addView(date)
        content.addView(actions)
        card.addView(content)
        return card
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

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
