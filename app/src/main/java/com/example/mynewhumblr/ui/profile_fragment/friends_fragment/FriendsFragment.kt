package com.example.mynewhumblr.ui.profile_fragment.friends_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.databinding.FragmentFriendsBinding
import com.example.mynewhumblr.ui.LoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
@AndroidEntryPoint
class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by viewModels()
    private val friendsAdapter = FriendsAdapter(
        onDoNotBeFriendsClick = { children: UserFriends.Data.Children, position: Int ->
            onClickDoNotBeFriends(
                children,
                position
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        hideAppbarAndBottomView(requireActivity())
        binding.recyclerViewUserFriends.adapter =
            friendsAdapter.withLoadStateFooter(LoadStateAdapter())

        binding.swipeRefresh.setOnRefreshListener { friendsAdapter.refresh() }

        friendsAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.pageFriends.onEach {
            friendsAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun onClickDoNotBeFriends(children: UserFriends.Data.Children, position: Int) {
        viewModel.doNotMakeFriends(children.name)
        friendsAdapter.unfriendUser(position)
    }
}