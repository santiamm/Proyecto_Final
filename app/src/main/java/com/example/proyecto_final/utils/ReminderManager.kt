package com.example.proyecto_final.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.proyecto_final.receivers.ReminderReceiver

data class ScheduledReminder(
    val reportId: Int,
    val title: String,
    val triggerTime: Long
)

class ReminderManager(private val context: Context) {

    fun scheduleReminder(reportId: Int, title: String, delayInMillis: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("report_id", reportId)
                putExtra("report_title", title)
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, reportId, intent, flags)
            val triggerTime = System.currentTimeMillis() + delayInMillis
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            saveReminder(reportId, title, triggerTime)
            android.util.Log.d("ReminderManager", "Recordatorio programado para ID $reportId en ${delayInMillis}ms")
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("ReminderManager", "Error al programar: ${e.message}")
        }
    }

    fun cancelReminder(reportId: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, reportId, intent, flags)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            removeReminder(reportId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getScheduledReminders(): List<ScheduledReminder> {
        val prefs = context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
        return prefs.all.mapNotNull { (key, value) ->
            if (key.startsWith("time_")) return@mapNotNull null
            val reportId = key.toIntOrNull() ?: return@mapNotNull null
            val title = value as? String ?: return@mapNotNull null
            val triggerTime = prefs.getLong("time_$reportId", 0)
            if (triggerTime <= System.currentTimeMillis()) return@mapNotNull null
            ScheduledReminder(reportId, title, triggerTime)
        }.sortedBy { it.triggerTime }
    }

    private fun saveReminder(reportId: Int, title: String, triggerTime: Long) {
        context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString(reportId.toString(), title)
            .putLong("time_$reportId", triggerTime)
            .apply()
    }

    private fun removeReminder(reportId: Int) {
        context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove(reportId.toString())
            .remove("time_$reportId")
            .apply()
    }
}
