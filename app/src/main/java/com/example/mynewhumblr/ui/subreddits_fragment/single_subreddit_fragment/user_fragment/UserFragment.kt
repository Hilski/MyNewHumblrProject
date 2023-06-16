package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment.user_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.LoadState
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.models.ProfileModel
import com.example.mynewhumblr.databinding.FragmentSingleSubredditBinding
import com.example.mynewhumblr.databinding.FragmentUserBinding
import com.example.mynewhumblr.ui.subreddits_fragment.postsDelegate
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        ListDelegationAdapter(postsDelegate { subQuery: SubQuery, _: ListItem, clickableView: ClickableView ->
            onClick(subQuery, clickableView)
        })
    }

    var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = arguments?.getString("MyArg").toString()
 //       Snackbar.make(binding.recycler, name, BaseTransientBottomBar.LENGTH_SHORT).show()
 //       Toast.makeText(context, name, Toast.LENGTH_LONG).show()

        getLoadingState(name)
        setMakeFriendsClick(name)
    }

    private fun getLoadingState(name: String) {
        viewModel.getProfileAndContent(name)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state -> updateUi(state) }
        }
    }

    private fun updateUi(state: LoadState) {
        when (state) {
            LoadState.NotStartedYet -> {}
            LoadState.Loading -> {
                binding.containerView.isVisible = false
                binding.common.progressBar.isVisible = true
                binding.common.error.isVisible = false

                Snackbar.make(binding.recycler, "Loading", BaseTransientBottomBar.LENGTH_SHORT).show()
            }

            is LoadState.Error -> {
                binding.containerView.isVisible = false
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = true

                Snackbar.make(binding.recycler, "Error", BaseTransientBottomBar.LENGTH_SHORT).show()
            }

            is LoadState.Content -> {

 //               Snackbar.make(binding.recycler, "Content", BaseTransientBottomBar.LENGTH_SHORT).show()

                binding.containerView.isVisible = true
                binding.common.progressBar.isVisible = false
                binding.common.error.isVisible = false
                val data = state.data as ProfileModel
                Snackbar.make(binding.recycler, data.urlAvatar.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                if (data.urlAvatar != null) loadAvatar(data.urlAvatar!!)
                loadProfileTexts(data)
                loadUserContent(state.data2 as List<ListItem>)
            }
            else -> {}
        }
    }

    private fun loadAvatar(url: String) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder)
            .into(binding.imageView)
        Snackbar.make(binding.recycler, url, BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun loadProfileTexts(data: ProfileModel) {
        with(binding) {
            userName.text = data.name
            userId.text = getString(R.string.user_id, data.id)
            karma.text = getString(R.string.karma, data.total_karma ?: 0)
            followers.text =
                getString(R.string.followers, data.more_infos?.subscribers ?: "0")
        }
    }

    private fun loadUserContent(data: List<ListItem>) {

        binding.recycler.adapter = adapter
        adapter.items = data
 //       Snackbar.make(binding.recycler, data.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()

    }

    private fun setMakeFriendsClick(name: String) {
        binding.buttonMakeFriends.setOnClickListener {
            viewModel.makeFriends(name)
            Snackbar.make(
                binding.containerView, getString(R.string.friends_now),
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
        }
    }

    private fun onClick(subQuery: SubQuery, clickableView: ClickableView) {
        when (clickableView) {
            ClickableView.SAVE -> viewModel.savePost(postName = subQuery.name)
            ClickableView.UNSAVE -> viewModel.unsavePost(postName = subQuery.name)
            ClickableView.VOTE ->
                viewModel.votePost(voteDirection = subQuery.voteDirection, postName = subQuery.name)

            else -> {}
        }
    }
}