package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

interface PostRepository {
    fun getAll() : List<Post>
    fun likeById(id: Long): Post

    fun unLikeById(id: Long): Post
    fun shareById(id: Long)
    fun removeById(id: Long)

    fun findById(id: Long): Post?

    fun save(post: Post)



}