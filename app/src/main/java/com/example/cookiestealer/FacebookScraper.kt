package com.example.cookiestealer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONArray

class FacebookScraper(private val context: Context) {

    private var webView: WebView? = null
    private val handler = Handler(Looper.getMainLooper())

    fun scrapeStoryViewers(cookies: String, callback: (List<String>) -> Unit) {
        setupWebView(cookies)

        webView?.loadUrl("https://m.facebook.com/stories/highlights/")

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                handler.postDelayed({
                    extractNames { names ->
                        callback(names)
                        cleanup()
                    }
                }, 3000) // Đợi 3 giây cho trang load xong
            }
        }
    }

    fun scrapePostLikes(cookies: String, postUrl: String, callback: (List<String>) -> Unit) {
        setupWebView(cookies)

        webView?.loadUrl(postUrl)

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Click vào nút "Lượt thích"
                view?.evaluateJavascript(
                    """
                    (function() {
                        var likeButton = document.querySelector('[aria-label*="thích"]');
                        if (likeButton) likeButton.click();
                    })();
                """.trimIndent(), null)

                handler.postDelayed({
                    extractNames { names ->
                        callback(names)
                        cleanup()
                    }
                }, 3000)
            }
        }
    }

    private fun setupWebView(cookies: String) {
        webView = WebView(context)
        webView?.settings?.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        // Set cookies
        val cookieManager = CookieManager.getInstance()
        cookies.split(";").forEach { cookie ->
            cookieManager.setCookie("facebook.com", cookie.trim())
        }
        CookieManager.getInstance().flush()
    }

    private fun extractNames(callback: (List<String>) -> Unit) {
        webView?.evaluateJavascript(
            """
            (function() {
                var names = [];
                
                // Tìm tất cả link chứa tên người dùng
                var links = document.querySelectorAll('a[href*="/"]');
                
                links.forEach(function(link) {
                    var text = link.textContent.trim();
                    // Lọc tên hợp lệ (2-50 ký tự, có chữ cái)
                    if (text.length >= 2 && text.length <= 50 && /[a-zA-ZÀ-ỹ]/.test(text)) {
                        names.push(text);
                    }
                });
                
                // Loại bỏ trùng lặp
                return JSON.stringify([...new Set(names)]);
            })();
        """.trimIndent()
        ) { result ->
            try {
                val cleanResult = result?.replace("\\", "")?.trim('"') ?: "[]"
                val jsonArray = JSONArray(cleanResult)
                val names = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {
                    names.add(jsonArray.getString(i))
                }

                callback(names)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(emptyList())
            }
        }
    }

    private fun cleanup() {
        webView?.destroy()
        webView = null
    }
}

