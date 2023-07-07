package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.util.Calendar

class PostRepositoryFileImpl(
    private val context: Context
): PostRepository {

    private val gson = Gson()
    private val fileName = "posts.json"
    private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type

    private var nextId = 1L
    private var posts = emptyList<Post>()
    set(value) {
        field = value
        sync()
    }

    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(fileName)

        if(file.exists()) {
            context.openFileInput(fileName).bufferedReader().use {
                posts = gson.fromJson(it, typeToken)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                data.value = posts
            }
        }

    }

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )

        }
        data.value = posts

    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts

    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts

    }
    override fun findById(id: Long): Post? {
        return posts.find { it.id == id }

    }

    override fun save(post: Post) {
        if (post.id == 0L) {

            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = Calendar.getInstance().time.toString()


                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts

    }

    private fun sync() {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }


}