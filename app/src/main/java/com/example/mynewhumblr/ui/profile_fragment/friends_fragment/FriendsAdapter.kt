package com.example.mynewhumblr.ui.profile_fragment.friends_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.databinding.ItemFriendsBinding

class FriendsAdapter (
    private val onDoNotBeFriendsClick: (UserFriends.Data.Children, position: Int) -> Unit,
    ) : PagingDataAdapter<UserFriends.Data.Children, FriendsViewHolder>(DiffUtilCallbackFriends()) {

        fun unfriendUser(position: Int) {
            notifyItemRemoved(position)
        }

        override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                item?.let {
                    Glide
                        .with(imageViewUserFriendAvatar.context)
                        // TODO: load a real avatar of the user
                        .load(it.id)
                        .placeholder(R.drawable.ic_r_humblr)
                        .into(imageViewUserFriendAvatar)
                    textViewUserFriendName.text = it.name
                    buttonUserFriendsDoNotBeFriend.setOnClickListener {
                        onDoNotBeFriendsClick.invoke(item, position)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
            return FriendsViewHolder(
                ItemFriendsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    class DiffUtilCallbackFriends : DiffUtil.ItemCallback<UserFriends.Data.Children>() {
        override fun areItemsTheSame(oldItem: UserFriends.Data.Children, newItem: UserFriends.Data.Children): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserFriends.Data.Children, newItem: UserFriends.Data.Children): Boolean =
            oldItem == newItem
    }

    class FriendsViewHolder(val binding: ItemFriendsBinding) : RecyclerView.ViewHolder(binding.root)