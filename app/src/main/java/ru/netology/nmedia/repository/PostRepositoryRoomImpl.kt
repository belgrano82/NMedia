package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.util.concurrent.TimeUnit

class PostRepositoryRoomImpl(
    private val dao: PostDao,
) : PostRepository {

    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val type = object :TypeToken<List<Post>>() {}.type

    private val gson = Gson()

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val mediaType = "application/json".toMediaType()

    }
    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()
     return   client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: error("Body is null")
            }
            .let {
                gson.fromJson(it, type)
            }

    }


    override fun save(post: Post) {
//        val request = Request.Builder()
//            .url("${BASE_URL}/api/slow/posts")
//            .post(gson.toJson(post).toRequestBody(mediaType))
//            .build()
//
//        return client.newCall(request)
//            .execute()
//            .let {
//                it.body?.string() ?: error("Body is null")
//            }.let {
//                gson.fromJson(it, Post::class.java)
//            }
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(mediaType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()

    }

    override fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .execute()
            .body?.string()
            ?.let {
                gson.fromJson(it, Post::class.java)
            } ?: throw java.lang.RuntimeException("body is null") }

    override fun unLikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .execute()
            .body?.string()
            ?.let {
                gson.fromJson(it, Post::class.java)
            } ?: throw java.lang.RuntimeException("body is null") }

    override fun shareById(id: Long) = dao.shareById(id)

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun findById(id: Long): Post? = dao.findById(id)
}
