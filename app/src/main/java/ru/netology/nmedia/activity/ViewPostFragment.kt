package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentViewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.formatNumber
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class ViewPostFragment : Fragment() {


    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    companion object {
        var Bundle.textArg: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewPostBinding.inflate(
            inflater, container, false
        )


        var post = viewModel.findById(requireArguments().getLong("postId"))!!



        binding.apply {
            content.text = post.content
            author.text = post.author
            published.text = post.published.toString()
            like.text = formatNumber(post.likes)
            share.text = formatNumber(post.shares)
            like.isChecked = post.likedByMe
            viewsCount.text = post.views.toString()

            like.setOnClickListener {
                viewModel.likeById(post, context = requireContext())
                post =
                    if (like.isChecked) post.copy(likes = post.likes + 1) else post.copy(likes = post.likes - 1)
                like.text = formatNumber(post.likes)
            }

            share.setOnClickListener {
                viewModel.shareById(post.id)
                post = post.copy(shares = post.shares + 1)
                share.text = formatNumber(post.shares)

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)

            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                findNavController().navigateUp()
                                viewModel.removeById(post.id, context = requireContext())

                                true
                            }
                            R.id.edit -> {
                                viewModel.edit(post)
                                viewModel.changeContent(binding.content.text.toString())

                                findNavController().navigate(
                                    R.id.action_postViewFragment_to_newPostFragment,
                                    Bundle().apply {

                                        putString("postContent", post.content)

                                    })


                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

        }
        return binding.root
    }
}