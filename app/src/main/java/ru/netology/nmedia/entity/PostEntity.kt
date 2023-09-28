package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import java.util.*

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val author: String,
    var content: String,
    val published: Long,
    val likes: Int,
    var shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val video: String? = null
) {
    fun toDto() = Post(id, author, content, published, likes, shares, views, likedByMe, video)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.shares, dto.views, dto.likedByMe, dto.video)

    }
}