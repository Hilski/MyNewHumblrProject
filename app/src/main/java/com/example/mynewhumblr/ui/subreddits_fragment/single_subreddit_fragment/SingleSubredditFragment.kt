package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.LoadState
import com.example.mynewhumblr.data.MY_ARG
import com.example.mynewhumblr.data.POST_ID
import com.example.mynewhumblr.data.SUBSCRIBE
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.UNSUBSCRIBE
import com.example.mynewhumblr.data.models.SubredditModel
import com.example.mynewhumblr.databinding.FragmentSingleSubredditBinding
import com.example.mynewhumblr.ui.subreddits_fragment.SubredditsPagedDataDelegationAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleSubredditFragment : Fragment() {

    private val viewModel: SingleSubredditViewModel by viewModels()
    private var _binding: FragmentSingleSubredditBinding? = null
    private val binding get() = _binding!!
    val bundle = Bundle()

    private val adapter by lazy {
        SubredditsPagedDataDelegationAdapter { subQuery: SubQuery, _: ListItem, clickableView: ClickableView ->
            onClick(subQuery, clickableView)
        }
    }
    var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleSubredditBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = arguments?.getString(MY_ARG).toString()

        loadPosts()
        getLoadingState(name)
        setToolbarBackButton()
    }

    private fun loadPosts() {
        binding.recycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getPostsList(name).collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun getLoadingState(name: String) {
        viewModel.getSubredditInfo(name)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state -> updateUi(state) }
        }
    }

    private fun updateUi(state: LoadState) {
        when (state) {
            LoadState.NotStartedYet -> {}
            LoadState.Loading -> {
                binding.recycler.isVisible = false
                binding.common.progressBar.isVisible = true
                binding.common.error.isVisible = false
            }

            is LoadState.Error -> {
                binding.recycler.isVisible = false
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = true
            }

            is LoadState.Content -> {
                binding.recycler.isVisible = true
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = false
                val data = state.data as SubredditModel
                loadSubredditDescription(data)
                setExpandButtonClick()
                setShareButtonClick(data)
                binding.subscribeButton.isSelected = data.isUserSubscriber == true
                setSubscribeButtonClick(data)
            }
        }
    }

    private fun loadSubredditDescription(subreddit: SubredditModel) {
        binding.subredditName.text = subreddit.namePrefixed
        binding.subscribers.text = getString(R.string.subscribers, subreddit.subscribers ?: 0)
        binding.subredditDescription.text = subreddit.description
    }

    private fun setExpandButtonClick() {
        binding.expandButton.setOnClickListener {
            when (binding.detailedInfo.visibility) {
                View.GONE -> binding.detailedInfo.visibility = View.VISIBLE
                View.VISIBLE -> binding.detailedInfo.visibility = View.GONE
                View.INVISIBLE -> {}
            }
        }
    }

    private fun setShareButtonClick(data: SubredditModel) {
        binding.shareButton.setOnClickListener {
            shareLinkOnSubreddit(getString(R.string.share_url, data.url ?: ""))
        }
    }

    private fun setSubscribeButtonClick(data: SubredditModel) {
        binding.subscribeButton.setOnClickListener {
            binding.subscribeButton.isSelected = !binding.subscribeButton.isSelected
            val action = if (!binding.subscribeButton.isSelected) UNSUBSCRIBE else SUBSCRIBE
            onClick(SubQuery(name = data.name, action = action), ClickableView.SUBSCRIBE)
        }
    }

    private fun shareLinkOnSubreddit(url: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = getString(R.string.share_text)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
    }

    private fun onClick(subQuery: SubQuery, clickableView: ClickableView) {
        when (clickableView) {
            ClickableView.SAVE -> viewModel.savePost(postName = subQuery.name)
            ClickableView.UNSAVE -> viewModel.unsavePost(postName = subQuery.name)
            ClickableView.VOTE ->
                viewModel.votePost(voteDirection = subQuery.voteDirection, postName = subQuery.name)

            ClickableView.SUBSCRIBE -> {
                viewModel.subscribe(subQuery)
                val text =
                    if (subQuery.action == SUBSCRIBE) getString(R.string.subscribed)
                    else getString(R.string.unsubscribed)
                Snackbar.make(binding.recycler, text, BaseTransientBottomBar.LENGTH_SHORT).show()
            }

            ClickableView.USER -> {
                bundle.putString(MY_ARG, subQuery.name)
                findNavController().navigate(
                    R.id.action_singleSubredditFragment_to_userFragment,
                    bundle
                )

             }
            ClickableView.SUBREDDIT -> {
                bundle.putString(POST_ID, subQuery.id)

                findNavController().navigate(
                    R.id.action_singleSubredditFragment_to_singleSubredditCommentsFragment,
                    bundle
                )
            }
        }
    }

    private fun setToolbarBackButton() {
        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack(this)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}