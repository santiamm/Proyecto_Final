package com.example.proyecto_final.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
            val remindersSet = prefs.all
            for ((key, value) in remindersSet) {
                val reportId = key.toIntOrNull() ?: continue
                val title = value as? String ?: continue
                val triggerTime = prefs.getLong("time_$reportId", 0)
                if (triggerTime > System.currentTimeMillis()) {
                    rescheduleReminder(context, reportId, title, triggerTime)
                }
            }
        }
    }

    private fun rescheduleReminder(context: Context, reportId: Int, title: String, triggerTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("report_id", reportId)
            putExtra("report_title", title)
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, reportId, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }
}