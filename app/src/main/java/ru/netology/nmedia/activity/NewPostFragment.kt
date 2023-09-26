package ru.netology.nmedia.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var draftKey: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        draftKey = "draftContent"

        arguments?.textArg
            ?.let(binding.edit::setText)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val draftText = binding.edit.text.toString()
                    saveDraftText(draftText)

                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            })

        val draftText = getDraftText()
        binding.edit.setText(draftText)


        val content = arguments?.getString("postContent")
        binding.apply {
            if (content != null) {
                edit.setText(content)
            }
        }



        binding.cancel.setOnClickListener {
            viewModel.undoEditing()
            findNavController().navigateUp()
        }


        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())

        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            val result2 = binding.edit.text

            setFragmentResult("editText2", bundleOf("textEdit2" to result2))
            saveDraftText("")

            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun saveDraftText(draftText: String) {
        val editor = sharedPreferences.edit()
        editor.putString(draftKey, draftText)
        editor.apply()
    }

    private fun getDraftText(): String {
        return sharedPreferences.getString(draftKey, "") ?: ""
    }
}