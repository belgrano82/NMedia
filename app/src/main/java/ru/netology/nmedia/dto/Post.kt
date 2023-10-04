package ru.netology.nmedia.dto


data class Post(
    var id: Long,
    val author: String,
    val authorAvatar: String = "",
    var content: String,
    val published: Long,
    val likes: Int,
    var shares: Int,
    val views: Int,
    var likedByMe: Boolean = false,
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



