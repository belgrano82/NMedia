package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {


    override fun getAllAsync(callBack: PostRepository.CallBack<List<Post>>) {
        PostApi.service.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callBack.onError(RuntimeException(response.errorBody()?.string()))
                    return
                }

                val body = response.body() ?: run {
                    callBack.onError(RuntimeException("responce is empty"))
                    return
                }

                callBack.onSuccess(body)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callBack.onError(Exception(t))
            }
        })
    }

    override fun saveAsync(post: Post, callBack: PostRepository.CallBack<Unit>) {
        PostApi.service.savePost(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callBack.onError(RuntimeException(response.errorBody()?.string()))
                    return
                }

                callBack.onSuccess(Unit)

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callBack.onError(Exception(t))
            }

        })
    }

    override fun likeByIdAsync(id: Long, callBack: PostRepository.CallBack<Post>) {

        PostApi.service.likePost(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        callBack.onSuccess(post)
                    } else {
                        callBack.onError(RuntimeException("Response body is null"))
                    }
                } else {
                    callBack.onError(RuntimeException("Request failed with code ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callBack.onError(Exception(t))
            }
        })
    }

    override fun unLikeByIdAsync(id: Long, callBack: PostRepository.CallBack<Post>) {

        PostApi.service.unlikePost(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        callBack.onSuccess(post)
                    } else {
                        callBack.onError(RuntimeException(response.errorBody()?.toString()))
                    }
                } else {
                    callBack.onError(RuntimeException(response.errorBody()?.toString()))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callBack.onError(Exception(t))
            }
        })
    }


    override fun shareById(id: Long) = dao.shareById(id)


    override fun removeByIdAsync(id: Long, callBack: PostRepository.CallBack<Unit>) {
        PostApi.service.deletePost(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {

                if (!response.isSuccessful) {
                    callBack.onError(RuntimeException(response.errorBody()?.string()))
                    return
                } else {
                    callBack.onSuccess(Unit)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callBack.onError(Exception(t))
            }

        })
    }

    override fun findById(id: Long): Post? = dao.findById(id)
}

