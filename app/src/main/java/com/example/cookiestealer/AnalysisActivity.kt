package com.example.cookiestealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AnalysisActivity : AppCompatActivity() {

    private lateinit var containerTopFriends: LinearLayout
    private lateinit var cardNoData: CardView
    private lateinit var tvNoData: TextView
    private lateinit var tvStats: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnStartService: Button
    private lateinit var database: InteractionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        database = InteractionDatabase(this)

        containerTopFriends = findViewById(R.id.containerTopFriends)
        cardNoData = findViewById(R.id.cardNoData)
        tvNoData = findViewById(R.id.tvNoData)
        tvStats = findViewById(R.id.tvStats)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnStartService = findViewById(R.id.btnStartService)

        btnRefresh.setOnClickListener {
            loadAnalysis()
        }

        btnStartService.setOnClickListener {
            startCollectorService()
        }

        loadAnalysis()
        checkServiceStatus()

        if (database.getTotalRecords() == 0) {
            // Insert fake data để test
            insertFakeData()
        }
    }

    private fun loadAnalysis() {
        containerTopFriends.removeAllViews()
        
        // Lấy dữ liệu từ database
        val stats = database.getTopInteractions(days = 30, limit = 20)
        
        // Hiển thị thống kê tổng quan
        val totalRecords = database.getTotalRecords()
        val dbSize = database.getDatabaseSizeMB()
        
        tvStats.text = """
            📊 Dữ liệu: $totalRecords tương tác
            💾 Dung lượng: ${String.format("%.2f", dbSize)} MB
            📅 Phân tích: 30 ngày gần nhất
        """.trimIndent()

        if (stats.isEmpty()) {
            cardNoData.visibility = View.VISIBLE
            tvNoData.text = "Chưa có dữ liệu"
            return
        }

        cardNoData.visibility = View.GONE

        // Hiển thị top người tương tác
        stats.forEachIndexed { index, user ->
            val itemView = layoutInflater.inflate(
                R.layout.item_friend_analysis,
                containerTopFriends,
                false
            )

            val tvRank = itemView.findViewById<TextView>(R.id.tvRank)
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvStoryViews = itemView.findViewById<TextView>(R.id.tvStoryViews)
            val tvPostLikes = itemView.findViewById<TextView>(R.id.tvPostLikes)
            val tvPostComments = itemView.findViewById<TextView>(R.id.tvPostComments)
            val tvInteractionScore = itemView.findViewById<TextView>(R.id.tvInteractionScore)

            tvRank.text = "${index + 1}"
            tvName.text = user.name
            tvStoryViews.text = "${user.storyViews}"
            tvPostLikes.text = "${user.postLikes}"
            tvPostComments.text = "${user.comments}"
            tvInteractionScore.text = "${user.totalScore}"

            // Animation cho từng item
            val animation = AnimationUtils.loadAnimation(this, R.anim.item_animation)
            animation.startOffset = (index * 50).toLong()
            itemView.startAnimation(animation)

            containerTopFriends.addView(itemView)
        }
    }

    private fun checkServiceStatus() {
        val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
        val serviceRunning = prefs.getBoolean("collector_running", false)
        
        if (serviceRunning) {
            btnStartService.text = "⏸️ Dừng Thu thập"
        } else {
            btnStartService.text = "▶️ Thu thập"
        }
    }

    private fun startCollectorService() {
        val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
        val isRunning = prefs.getBoolean("collector_running", false)
        
        if (isRunning) {
            // Dừng service
            val intent = Intent(this, DataCollectorService::class.java)
            stopService(intent)
            prefs.edit().putBoolean("collector_running", false).apply()
            btnStartService.text = "▶️ Thu thập"
        } else {
            // Bắt đầu service
            val intent = Intent(this, DataCollectorService::class.java)
            startService(intent)
            prefs.edit().putBoolean("collector_running", true).apply()
            btnStartService.text = "⏸️ Dừng Thu thập"
        }
    }

    private fun insertFakeData() {
        database.insertStoryView("100001", "Nguyễn Văn A", System.currentTimeMillis())
        database.insertStoryView("100001", "Nguyễn Văn A", System.currentTimeMillis() - 86400000)
        database.insertPostLike("100001", "post1", System.currentTimeMillis())
        database.insertPostLike("100001", "post1", System.currentTimeMillis() - 172800000)
        database.insertPostComment("100001", "post1", "Nice!", System.currentTimeMillis())
        
        database.insertStoryView("100002", "Trần Thị B", System.currentTimeMillis())
        database.insertPostLike("100002", "post2", System.currentTimeMillis())
    }
}

