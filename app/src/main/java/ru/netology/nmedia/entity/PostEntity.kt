package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AttachmentConverter

@Entity
@TypeConverters(AttachmentConverter::class)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val author: String,
    val authorAvatar: String,
    var content: String,
    val published: Long,
    val likes: Int,
    var shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val video: String? = null,
    val attachment: Attachment? = null
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likes, shares, views, likedByMe, video, attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likes, dto.shares, dto.views, dto.likedByMe, dto.video, dto.attachment)

    }
}



