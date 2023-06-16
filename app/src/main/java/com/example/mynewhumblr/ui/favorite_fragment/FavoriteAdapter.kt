package com.example.mynewhumblr.ui.favorite_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.ApiResult
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.databinding.ItemFavoriteSubredditBinding
import com.example.mynewhumblr.databinding.ItemSubredditBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SubredditAdapter(
    private val onClick: (SubredditListing.SubredditListingData.Subreddit) -> Unit,
    private val onClickSubscribe: (displayName: String, userIsSubscribed: Boolean, position: Int) -> Unit,
    private val onClickShare: (String) -> Unit
) : PagingDataAdapter<SubredditListing.SubredditListingData.Subreddit, SubredditViewHolder>(DiffUtilCallbackChildren()) {

    fun updateElement(data: ApiResult<Int>) {
        data.data?.let { position ->
            snapshot()[position]?.let {
                if (it.data.user_is_subscriber) {
                    it.data.user_is_subscriber = false
                    it.data.subscribers--
                } else {
                    it.data.user_is_subscriber = true
                    it.data.subscribers++
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            item?.let {
                Glide
                    .with(imageViewLogo)
                    .load(it.data.icon_img)
                    .placeholder(R.drawable.ic_r_humblr)
                    .circleCrop()
                    .into(imageViewLogo)
                textViewName.text = it.data.display_name_prefixed
                textViewDescription.text = it.data.public_description
                textViewSubscribers.text = it.data.subscribers.toString()
                if (it.data.user_is_subscriber) {
                    imageViewSubscribeButton.setImageResource(R.drawable.ic_subscribed)
                } else {
                    imageViewSubscribeButton.setImageResource(R.drawable.ic_subscribe)
                }
            }
            root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    item?.let {
                        onClick.invoke(it)
                    }
                }
            }
            imageViewShareButton.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    item?.let {
                        it.data.url?.let { it1 -> onClickShare.invoke(it1) }
                    }
                }
            }
            imageViewSubscribeButton.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    item?.let {
                        onClickSubscribe.invoke(
                            it.data.display_name,
                            it.data.user_is_subscriber,
                            position
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        return SubredditViewHolder(
            ItemFavoriteSubredditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

class DiffUtilCallbackChildren : DiffUtil.ItemCallback<SubredditListing.SubredditListingData.Subreddit>() {
    override fun areItemsTheSame(oldItem: SubredditListing.SubredditListingData.Subreddit, newItem: SubredditListing.SubredditListingData.Subreddit): Boolean =
        oldItem.data.id == newItem.data.id

    override fun areContentsTheSame(oldItem: SubredditListing.SubredditListingData.Subreddit, newItem: SubredditListing.SubredditListingData.Subreddit): Boolean =
        oldItem == newItem
}

class SubredditViewHolder(val binding: ItemFavoriteSubredditBinding) : RecyclerView.ViewHolder(binding.root)