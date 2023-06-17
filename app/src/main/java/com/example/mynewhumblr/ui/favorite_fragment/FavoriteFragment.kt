package com.example.mynewhumblr.ui.favorite_fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.BANNER_IMAGE_KEY
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.DISPLAY_NAME_KEY
import com.example.mynewhumblr.data.ICON_KEY
import com.example.mynewhumblr.data.IS_SUBSCRIBER_KEY
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.SUBSCRIBE
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.models.ApiResult
import com.example.mynewhumblr.data.models.PostListing
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.UiText
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.databinding.FragmentFavoriteBinding
import com.example.mynewhumblr.databinding.FragmentProfileBinding
import com.example.mynewhumblr.ui.LoadStateAdapter
import com.example.mynewhumblr.ui.subreddits_fragment.SubredditsPagedDataDelegationAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.onEach
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel : FavoriteViewModel by viewModels()
    private val adapter by lazy { SubredditsPagedDataDelegationAdapter {
            subQuery: SubQuery, item: ListItem, clickableView: ClickableView ->
        onClick(subQuery, item, clickableView) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadContent()
        tabLayoutSelectedListener(binding.toggleType, false)
        tabLayoutSelectedListener(binding.toggleSource, true)
        loadStateItemsObserve()
    }

    private fun loadContent() {
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.thingList.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun loadStateItemsObserve() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { state ->
                binding.common.progressBar.isVisible =
                    state.refresh is LoadState.Loading || state.append is LoadState.Loading
                binding.common.error.isVisible =
                    state.refresh is LoadState.Error || state.append is LoadState.Error || state.prepend is LoadState.Error
                binding.noSavedPosts.isVisible =
                    state.refresh is LoadState.NotLoading && adapter.itemCount == 0
            }
        }
    }

    private fun tabLayoutSelectedListener(tabLayout: TabLayout, isSource: Boolean) {
        tabLayout.setSelectedTabListener { position ->
            viewModel.setQuery(position, isSource)
        }
    }

    private fun onClick(subQuery: SubQuery, item: ListItem, clickableView: ClickableView) {
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
                Snackbar.make(binding.recyclerView, text, BaseTransientBottomBar.LENGTH_SHORT)
                    .show()
            }
            ClickableView.USER -> viewModel.navigateToUser(this, subQuery.name)
            ClickableView.SUBREDDIT ->
                viewModel.navigateToSingleSubreddit(this, item)
        }
    }

    fun TabLayout.setSelectedTabListener(block: (position: Int) -> Unit){
        this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) { block(tab.position) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}