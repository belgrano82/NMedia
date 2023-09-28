package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryRoomImpl(
    private val dao: PostDao,
) : PostRepository {

    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val type = object : TypeToken<List<Post>>() {}.type

    private val gson = Gson()

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val mediaType = "application/json".toMediaType()

    }


    override fun getAllAsync(callBack: PostRepository.CallBack<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callBack.onSuccess(gson.fromJson(body, type))
                    } catch (e: Exception) {
                        callBack.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError()
                }
            })
    }

    override fun saveAsync(post: Post, callBack: PostRepository.CallBack<Unit>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(mediaType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callBack.onSuccess(posts = Unit)
                    } catch (e: Exception) {
                        callBack.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError()
                }
            })
    }


    override fun likeByIdAsync(id: Long, callBack: PostRepository.CallBack<Post>) {
        val request: Request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()
                            ?.let {
                                gson.fromJson(it, Post::class.java)
                            } ?: throw java.lang.RuntimeException("body is null")
                        callBack.onSuccess(body)
                    } catch (e: IOException) {
                        callBack.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError()
                }
            })
    }

    override fun unLikeByIdAsync(id: Long, callBack: PostRepository.CallBack<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()
                            ?.let {
                                gson.fromJson(it, Post::class.java)
                            } ?: throw java.lang.RuntimeException("body is null")
                        callBack.onSuccess(body)
                    } catch (e: IOException) {
                        callBack.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError()
                }
            })
    }

    override fun shareById(id: Long) = dao.shareById(id)


    override fun removeByIdAsync(id: Long, callBack: PostRepository.CallBack<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callBack.onSuccess(Unit)
                    } catch (e: Exception) {
                        callBack.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError()
                }
            })
    }

    override fun findById(id: Long): Post? = dao.findById(id)
}
