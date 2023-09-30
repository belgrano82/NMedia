package ru.netology.nmedia.dto

import android.media.Image
import java.util.Calendar
import java.util.Date
import androidx.room.TypeConverter
import com.google.gson.Gson


data class Post(
    var id: Long,
    val author: String,
    val authorAvatar: String = "",
    var content: String,
    val published: Long,
    val likes: Int,
    var shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val video: String? = null,
    var attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}



