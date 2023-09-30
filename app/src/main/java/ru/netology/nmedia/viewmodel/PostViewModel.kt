package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0, content = "", author = "Нетология", authorAvatar = "", likedByMe = false,
    likes = 0, published = 0, shares = 0, views = 0, video = "", attachment = null)


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

        _data.postValue(FeedModel(loading = true))

        repository.getAllAsync(object : PostRepository.CallBack<List<Post>> {

            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun save() {

        edited.value?.let {

            repository.saveAsync(it, object : PostRepository.CallBack<Unit> {

                override fun onSuccess(posts: Unit) {
                    _postCreated.postValue(Unit)
                }

                override fun onError() {
                    edited.postValue(empty)
                }
            })
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

        val old = _data.value?.posts.orEmpty()

        val callBack = object : PostRepository.CallBack<Post> {
            override fun onSuccess(posts: Post) {
                val updatedPosts = old.map {
                    if (it.id == post.id) {
                        posts
                    } else {
                        it
                    }
                }
                _data.postValue(_data.value?.copy(posts = updatedPosts))
            }

            override fun onError() {
                _data.postValue(_data.value?.copy(posts = old))
            }

        }

        if (post.likedByMe) {
            repository.unLikeByIdAsync(post.id, callBack)

        } else {
            repository.likeByIdAsync(post.id, callBack)
        }
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun findById(id: Long): Post? = repository.findById(id)

    fun removeById(id: Long) {

        val old = _data.value?.posts.orEmpty()

        repository.removeByIdAsync(id, object : PostRepository.CallBack<Unit> {

            override fun onSuccess(posts: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    ))
            }

            override fun onError() {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}


