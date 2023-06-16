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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.BANNER_IMAGE_KEY
import com.example.mynewhumblr.data.DISPLAY_NAME_KEY
import com.example.mynewhumblr.data.ICON_KEY
import com.example.mynewhumblr.data.IS_SUBSCRIBER_KEY
import com.example.mynewhumblr.data.models.ApiResult
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.UiText
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.databinding.FragmentFavoriteBinding
import com.example.mynewhumblr.databinding.FragmentProfileBinding
import com.example.mynewhumblr.ui.LoadStateAdapter
import kotlinx.coroutines.flow.onEach
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel : FavoriteViewModel by viewModels()
    private val subredditAdapter = SubredditAdapter(
        onClick = { children: SubredditListing.SubredditListingData.Subreddit -> onItemClick(children) },
        onClickSubscribe = { name, isSubscribed, position ->
            onSubscribeClick(
                name,
                isSubscribed,
                position
            )
        },
        onClickShare = { url: String -> onShareClick(url) }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun onShareClick(url: String) {
        val fullUrl = buildString {
            this
                .append("www.reddit.com")
                .append(url)
        }
        val intent = Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_TEXT, fullUrl)
            it.type = "text/plain"
        }
        try {
            requireContext().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun onSubscribeClick(name: String, isSubscribed: Boolean, position: Int) {
        viewModel.subscribeUnsubscribe(name, isSubscribed, position)
    }
    private fun onItemClick(children: SubredditListing.SubredditListingData.Subreddit) {
        val bundle = Bundle()
        bundle.putString(DISPLAY_NAME_KEY, children.data.display_name)
        bundle.putString(BANNER_IMAGE_KEY, children.data.banner_img)
        bundle.putString(ICON_KEY, children.data.icon_img)
        bundle.putBoolean(IS_SUBSCRIBER_KEY, children.data.user_is_subscriber)

        //Добавить переход
//        findNavController().navigate(R.id.action_favoriteFragment_to_postsFragment, bundle)
        Toast.makeText(requireContext(), children.data.id, Toast.LENGTH_SHORT).show()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        showBottomView(requireActivity())
        binding.recyclerViewSubreddits.adapter =
            subredditAdapter.withLoadStateFooter(LoadStateAdapter())

        binding.swipeRefreshFavorite.setOnRefreshListener { subredditAdapter.refresh() }

        subredditAdapter.loadStateFlow.onEach {
            binding.swipeRefreshFavorite.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        loadFavoriteSubreddits()
        handleToggleButtons()
        observeSubscribeResult()
    }

    private fun loadFavoriteSubreddits() {
        viewModel.pageFavoriteSubredditChildren.onEach {
            subredditAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun loadFavoritePosts() {
        viewModel.pageFavoritePostChildren.onEach {
            subredditAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleToggleButtons() {
        with(binding) {
            toggleButtonSubreddits.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    loadFavoriteSubreddits()
                    toggleButtonPosts.isChecked = false
                    setupButtons(
                        R.color.white,
                        R.drawable.rectangle_8,
                        R.color.black,
                        R.drawable.rectangle_1
                    )
                }
            }
            toggleButtonPosts.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    loadFavoritePosts()
                    toggleButtonSubreddits.isChecked = false
                    setupButtons(
                        R.color.black,
                        R.drawable.rectangle_1,
                        R.color.white,
                        R.drawable.rectangle_8
                    )
                }
            }
        }
    }

    private fun observeSubscribeResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.subscribeChannel.collect { result ->
                    if (result is ApiResult.Error) {
                        Toast.makeText(requireContext(), UiText.ResourceString(R.string.something_went_wrong).asString(requireContext()), Toast.LENGTH_SHORT).show()
                    } else {
                        subredditAdapter.updateElement(result)
                    }
                }
            }
        }
    }
    private fun setupButtons(
        newColor: Int,
        newBackground: Int,
        popularColor: Int,
        popularBackground: Int
    ) {
        binding.toggleButtonSubreddits.setTextColor(
            resources.getColor(
                newColor,
                context?.theme
            )
        )
        binding.toggleButtonSubreddits.background = (ResourcesCompat.getDrawable(
            resources,
            newBackground,
            context?.theme
        ))
        binding.toggleButtonPosts.setTextColor(
            resources.getColor(
                popularColor,
                context?.theme
            )
        )
        binding.toggleButtonPosts.background = (ResourcesCompat.getDrawable(
            resources,
            popularBackground,
            context?.theme
        ))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}