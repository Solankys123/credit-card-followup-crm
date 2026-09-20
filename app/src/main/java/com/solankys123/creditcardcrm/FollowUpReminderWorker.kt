package com.solankys123.creditcardcrm

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class FollowUpReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val db = CrmDatabase.getInstance(applicationContext)
        val now = LocalDateTime.now()
        db.followUpDao().getAll().filter { !it.completed }.forEach { followUp ->
            parseDueAt(followUp.dueAt, now)?.let { due ->
                if (!due.isAfter(now)) notifyOnce(followUp.id, followUp.customerName, followUp.reason, followUp.priority, due, now)
            }
        }
        return Result.success()
    }

    private fun parseDueAt(value: String, now: LocalDateTime): LocalDateTime? {
        val match = Regex("^(Today|Tomorrow|Upcoming|Overdue), ([0-2]?[0-9]):([0-5][0-9]) ?(AM|PM)$", RegexOption.IGNORE_CASE)
            .matchEntire(value.trim()) ?: return null
        val day = match.groupValues[1].lowercase(Locale.ENGLISH)
        val hour = match.groupValues[2].toIntOrNull() ?: return null
        val minute = match.groupValues[3].toIntOrNull() ?: return null
        val ampm = match.groupValues[4].uppercase(Locale.ENGLISH)
        if (hour !in 1..12) return null
        var h = hour % 12
        if (ampm == "PM") h += 12
        val date = when (day) {
            "tomorrow" -> now.toLocalDate().plusDays(1)
            "today", "overdue" -> now.toLocalDate()
            else -> return null
        }
        return LocalDateTime.of(date, LocalTime.of(h, minute))
    }

    private fun notifyOnce(id: Long, customerName: String, reason: String, priority: String, due: LocalDateTime, now: LocalDateTime) {
        val prefs = applicationContext.getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val key = "followup_${id}_${due.toLocalDate()}_${due.toLocalTime()}"
        if (prefs.getBoolean(key, false)) return

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(applicationContext, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(if (due.isBefore(now)) "Overdue follow-up" else "Follow-up due")
            .setContentText("_${reason} • Priority _${priority}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Follow-up for customer _${customerName}: _${reason}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(id.toInt(), notification)
        prefs.edit().putBoolean(key, true).apply()
    }
}
