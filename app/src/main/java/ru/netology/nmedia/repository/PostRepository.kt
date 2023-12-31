package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun shareById(id: Long)

    fun findById(id: Long): Post?
    fun getAllAsync(callBack: CallBack<List<Post>>)
    fun saveAsync(post: Post, callBack: CallBack<Unit>)
//
    fun likeByIdAsync(id: Long, callBack: CallBack<Post>)
    fun unLikeByIdAsync(id: Long, callBack: CallBack<Post>)

    fun removeByIdAsync(id: Long, callBack: CallBack<Unit>)


    interface CallBack<T> {
        fun onSuccess (posts: T)
        fun onError(e: Exception)
    }
}