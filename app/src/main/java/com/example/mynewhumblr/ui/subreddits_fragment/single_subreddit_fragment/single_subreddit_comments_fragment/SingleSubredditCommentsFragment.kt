package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment.single_subreddit_comments_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.MY_ARG
import com.example.mynewhumblr.data.POST_ID
import com.example.mynewhumblr.data.repository.CommentsPagingSource
import com.example.mynewhumblr.databinding.FragmentSingleSubredditCommentsBinding
import com.example.mynewhumblr.ui.LoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SingleSubredditCommentsFragment : Fragment() {

    private var _binding: FragmentSingleSubredditCommentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommentsViewModel by viewModels()
    val bundle = Bundle()

    private val commentsAdapter = CommentsAdapter(
        onAuthorClick = { authorName: String -> onAuthorClick(authorName) }
    )
    private fun onAuthorClick(authorName: String) {
        bundle.putString(MY_ARG, authorName)
        findNavController().navigate(
            R.id.action_singleSubredditCommentsFragment_to_userFragment,
            bundle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleSubredditCommentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString(POST_ID)

        CommentsPagingSource.postId = postId.toString()
        setupCommentsAdapter()
    }
    private fun setupCommentsAdapter() {
        binding.recyclerViewComments.adapter =
            commentsAdapter.withLoadStateFooter(LoadStateAdapter())
        binding.swipeRefresh.setOnRefreshListener { commentsAdapter.refresh() }
        commentsAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.pageComments.onEach {
            commentsAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
