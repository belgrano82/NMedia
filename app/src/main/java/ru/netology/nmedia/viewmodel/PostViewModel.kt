package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0, content = "", author = "Нетология", likedByMe = false,
    likes = 0, shares = 0, views = 0, video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDb.getInstance(application).postDao
    )

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = _data
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val data = repository.getAll()

                FeedModel(posts = data, empty = data.isEmpty())
            } catch (e: Exception) {
                FeedModel(error = true)
            }.also {
                _data.postValue(it)
            }
        }
    }


    fun save() {
        thread {
            edited.value?.let {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
            edited.postValue(empty)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun undoEditing() {
        edited.value = empty
    }


    fun changeContent(content: String) {
        val text = content.trim()

        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        thread {
            try {
                val updated = if (post.likedByMe) {
                    repository.unLikeById(post.id)
                } else {
                    repository.likeById(post.id)
                }

                val newPosts = _data.value?.posts?.map {
                    if (it.id == post.id) {
                        updated
                    } else {
                        it
                    }
                }.orEmpty()

                _data.postValue(_data.value?.copy(posts = newPosts))
            } catch (e: Exception) {
            e.printStackTrace()
            }
        }

    }

    fun shareById(id: Long) = repository.shareById(id)

    fun findById(id: Long): Post? = repository.findById(id)

    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

}