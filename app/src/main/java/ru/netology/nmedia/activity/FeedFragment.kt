package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater, container, false
        )

        val refresh = binding.swipeRefresh

        refresh.setOnRefreshListener {
            viewModel.loadPosts()
            refresh.isRefreshing = false
        }

        val retry = binding.retry

        retry.setOnClickListener {
            viewModel.loadPosts()
        }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {

                        putString("postContent", post.content)

                    })
                viewModel.edit(post)
            }


            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlay(post: Post) {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))


                val playVideoIntent =
                    Intent.createChooser(intent, getString(R.string.description_play_button))
                startActivity(playVideoIntent)
            }

            override fun onViewPost(post: Post) {

                findNavController().navigate(
                    R.id.action_feedFragment_to_postViewFragment,
                    Bundle().apply {

                        putLong("postId", post.id)

                    })

                viewModel.findById(post.id)
            }


            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            binding.errorGroup.isVisible = state.error
            binding.empty.isVisible = state.empty
            binding.progressbar.isVisible = state.loading
            adapter.submitList(state.posts)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)

        }

        return binding.root
    }


}