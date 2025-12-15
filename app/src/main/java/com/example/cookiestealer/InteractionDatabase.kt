package com.example.cookiestealer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class InteractionDatabase(context: Context) : 
    SQLiteOpenHelper(context, "fb_interactions.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Bảng lưu tên người dùng (unique)
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE,
                first_seen INTEGER
            )
        """)
        
        // Bảng lưu lượt xem story
        db.execSQL("""
            CREATE TABLE story_views (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                viewed_at INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
        
        // Bảng lưu like/react
        db.execSQL("""
            CREATE TABLE post_likes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                post_url TEXT,
                liked_at INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
        
        // Bảng lưu comment
        db.execSQL("""
            CREATE TABLE post_comments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                post_url TEXT,
                commented_at INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS story_views")
        db.execSQL("DROP TABLE IF EXISTS post_likes")
        db.execSQL("DROP TABLE IF EXISTS post_comments")
        onCreate(db)
    }

    // ============ THÊM DỮ LIỆU ============
    
    fun addStoryView(name: String) {
        val userId = getOrCreateUser(name)
        val db = writableDatabase
        
        val values = ContentValues().apply {
            put("user_id", userId)
            put("viewed_at", System.currentTimeMillis())
        }
        
        db.insert("story_views", null, values)
    }

    fun addPostLike(name: String, postUrl: String) {
        val userId = getOrCreateUser(name)
        val db = writableDatabase
        
        val values = ContentValues().apply {
            put("user_id", userId)
            put("post_url", postUrl)
            put("liked_at", System.currentTimeMillis())
        }
        
        db.insert("post_likes", null, values)
    }

    fun addComment(name: String, postUrl: String) {
        val userId = getOrCreateUser(name)
        val db = writableDatabase
        
        val values = ContentValues().apply {
            put("user_id", userId)
            put("post_url", postUrl)
            put("commented_at", System.currentTimeMillis())
        }
        
        db.insert("post_comments", null, values)
    }

    private fun getOrCreateUser(name: String): Long {
        val db = writableDatabase
        
        // Kiểm tra user đã tồn tại chưa
        val cursor = db.rawQuery("SELECT id FROM users WHERE name = ?", arrayOf(name))
        
        return if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            cursor.close()
            id
        } else {
            cursor.close()
            // Tạo user mới
            val values = ContentValues().apply {
                put("name", name)
                put("first_seen", System.currentTimeMillis())
            }
            db.insert("users", null, values)
        }
    }

    // ============ PHÂN TÍCH DỮ LIỆU ============

    data class UserStats(
        val name: String,
        val storyViews: Int,
        val postLikes: Int,
        val comments: Int,
        val totalScore: Int
    )

    fun getTopInteractions(days: Int = 30, limit: Int = 10): List<UserStats> {
        val db = readableDatabase
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        
        val query = """
            SELECT 
                u.name,
                COUNT(DISTINCT sv.id) as story_count,
                COUNT(DISTINCT pl.id) as like_count,
                COUNT(DISTINCT pc.id) as comment_count,
                (COUNT(DISTINCT sv.id) * 1 + 
                 COUNT(DISTINCT pl.id) * 2 + 
                 COUNT(DISTINCT pc.id) * 3) as total_score
            FROM users u
            LEFT JOIN story_views sv ON u.id = sv.user_id AND sv.viewed_at > ?
            LEFT JOIN post_likes pl ON u.id = pl.user_id AND pl.liked_at > ?
            LEFT JOIN post_comments pc ON u.id = pc.user_id AND pc.commented_at > ?
            GROUP BY u.name
            HAVING total_score > 0
            ORDER BY total_score DESC
            LIMIT ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(
            cutoffTime.toString(),
            cutoffTime.toString(),
            cutoffTime.toString(),
            limit.toString()
        ))
        
        val results = mutableListOf<UserStats>()
        while (cursor.moveToNext()) {
            results.add(UserStats(
                name = cursor.getString(0),
                storyViews = cursor.getInt(1),
                postLikes = cursor.getInt(2),
                comments = cursor.getInt(3),
                totalScore = cursor.getInt(4)
            ))
        }
        cursor.close()
        
        return results
    }

    fun getTotalRecords(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT 
                (SELECT COUNT(*) FROM story_views) +
                (SELECT COUNT(*) FROM post_likes) +
                (SELECT COUNT(*) FROM post_comments) as total
        """, null)
        
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getDatabaseSizeMB(): Double {
        val dbFile = java.io.File(readableDatabase.path)
        return dbFile.length() / (1024.0 * 1024.0)
    }

    // ============ DỌN DẸP DỮ LIỆU ============

    fun cleanOldData(daysToKeep: Int = 90) {
        val db = writableDatabase
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        
        db.execSQL("DELETE FROM story_views WHERE viewed_at < ?", arrayOf(cutoffTime))
        db.execSQL("DELETE FROM post_likes WHERE liked_at < ?", arrayOf(cutoffTime))
        db.execSQL("DELETE FROM post_comments WHERE commented_at < ?", arrayOf(cutoffTime))
        
        // Xóa user không còn hoạt động
        db.execSQL("""
            DELETE FROM users WHERE id NOT IN (
                SELECT DISTINCT user_id FROM story_views
                UNION
                SELECT DISTINCT user_id FROM post_likes
                UNION
                SELECT DISTINCT user_id FROM post_comments
            )
        """)
        
        // Thu gọn database
        db.execSQL("VACUUM")
    }
}

