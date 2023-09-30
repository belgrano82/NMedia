package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.formatNumber
import ru.netology.nmedia.util.load
import java.util.*


interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onPlay(post: Post)
    fun onViewPost(post: Post)

//    fun onRefresh(post: Post)

}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        binding.apply {


            author.text = post.author
            published.text = Date(post.published * 1000L).toString()
            content.text = post.content
            like.text = formatNumber(post.likes)
            share.text = formatNumber(post.shares)
            viewsCount.text = formatNumber(post.views)
            binding.author.text = post.author
            group.visibility = if (post.video.isNullOrBlank()) View.GONE else View.VISIBLE
            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
                binding.attachment.load("http://10.0.2.2:9999/images/${post.attachment!!.url}")
            } else {
                attachment.visibility = View.GONE
            }


            binding.avatar.load("http://10.0.2.2:9999/avatars/${post.authorAvatar}", true)

            play.setOnClickListener {
                onInteractionListener.onPlay(post)
            }
            like.isChecked = post.likedByMe


            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }


            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            listOf(content, avatar, avatarVideo, published, author).forEach {
                it.setOnClickListener {
                    onInteractionListener.onViewPost(post)
                }
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

}
