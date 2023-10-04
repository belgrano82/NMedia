package ru.netology.nmedia.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.util.showErrorMessage

private val empty = Post(
    id = 0, content = "", author = "Нетология", authorAvatar = "", likedByMe = false,
    likes = 0, published = 0, shares = 0, views = 0, video = "", attachment = null
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
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
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                _data.value = FeedModel(error = true)
            }
        })
    }


    fun save(context: Context) {

        edited.value?.let {

            repository.saveAsync(it, object : PostRepository.CallBack<Unit> {

                override fun onSuccess(posts: Unit) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    showErrorMessage(
                        context,
                        "Упс... Не удалось поставить/убрать лайк. Попробуйте ещё раз через несколько секунд!"
                    )
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

    fun likeById(post: Post, context: Context) {

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

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
                showErrorMessage(
                    context,
                    "Упс... Не удалось поставить/убрать лайк. Попробуйте ещё раз через несколько секунд!"
                )

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

    fun removeById(id: Long, context: Context) {

        val oldPosts = _data.value?.posts.orEmpty()

        repository.removeByIdAsync(id, object : PostRepository.CallBack<Unit> {

            override fun onSuccess(posts: Unit) {

                val updatedPosts = oldPosts.filter { it.id != id }
                _data.value = _data.value?.copy(posts = updatedPosts)
            }

            override fun onError(e: Exception) {
                showErrorMessage(
                    context,
                    "Упс... Не удалось удалить пост. Попробуйте ещё раз через несколько секунд!"
                )

            }
        })
    }
}


