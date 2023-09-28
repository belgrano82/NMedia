package ru.netology.nmedia.dto

import java.util.Calendar
import java.util.Date


data class Post(
    var id: Long,
    val author: String,
    var content: String,
    val published: Long,
    val likes: Int,
    var shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val video: String? = null

)
