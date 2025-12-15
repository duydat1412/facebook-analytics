package com.example.cookiestealer

data class Friend(
    val id: String,
    val name: String,
    val picture: String,
    var interactionScore: Int = 0
)

object FacebookApiHelper {

    // Tạo dữ liệu giả để hiển thị
    fun generateFakeAnalysis(): List<Friend> {
        val fakeNames = listOf(
            "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D",
            "Hoàng Văn E", "Đặng Thị F", "Vũ Văn G", "Bùi Thị H",
            "Đỗ Văn I", "Ngô Thị K", "Dương Văn L", "Lý Thị M"
        )

        return fakeNames.mapIndexed { index, name ->
            Friend(
                id = "fake_$index",
                name = name,
                picture = "",
                interactionScore = (100 - index * 5) + (0..20).random()
            )
        }.sortedByDescending { it.interactionScore }
    }
}

