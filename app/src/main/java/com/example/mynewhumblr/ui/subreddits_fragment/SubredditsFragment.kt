package com.example.mynewhumblr.ui.subreddits_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.LoadState
import com.example.mynewhumblr.data.MY_ARG
import com.example.mynewhumblr.data.SUBSCRIBE
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.models.SubredditModel
import com.example.mynewhumblr.databinding.FragmentSubredditsBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubredditsFragment : Fragment() {

    private var _binding: FragmentSubredditsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubredditsViewModel by viewModels()
    val bundle = Bundle()
    private val adapter by lazy {
        SubredditsPagedDataDelegationAdapter { subQuery: SubQuery, item: ListItem, clickableView: ClickableView ->
            onClick(subQuery, item, clickableView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubredditsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadContent()
        getLoadingState()
        setSearchListener()

        binding.toggleSource.setSelectedTabListener { position ->
            viewModel.setSource(position)
        }
    }

    private fun loadContent() {
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.subredditsList.collect { pagingData -> adapter.submitData(pagingData) }
        }
    }

    private fun getLoadingState() {
        viewModel.getSubreddits()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state -> updateUi(state) }
        }
    }

    private fun updateUi(state: LoadState) {
        when (state) {
            LoadState.NotStartedYet -> {}
            LoadState.Loading -> {
                binding.common.progressBar.isVisible = true
                binding.common.error.isVisible = false
                binding.recyclerView.isVisible = false
            }

            is LoadState.Content -> {
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = false
                binding.recyclerView.isVisible = true

            }

            is LoadState.Error -> {
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }
    }

    private fun setSearchListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) viewModel.onSearchButtonClick(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun onClick(subQuery: SubQuery, item: ListItem, clickableView: ClickableView) {
        if (clickableView == ClickableView.SUBREDDIT) {
            val name = (item as SubredditModel).namePrefixed
            bundle.putString(MY_ARG, name)
            findNavController().navigate(
                R.id.action_subredditsFragment3_to_singleSubredditFragment,
                bundle
            )
        }
        if (clickableView == ClickableView.SUBSCRIBE) {
            viewModel.subscribe(subQuery)
            val text =
                if (subQuery.action == SUBSCRIBE) getString(R.string.subscribed)
                else getString(R.string.unsubscribed)
            Snackbar.make(binding.recyclerView, text, BaseTransientBottomBar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun TabLayout.setSelectedTabListener(block: (position: Int) -> Unit) {
        this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    block(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}