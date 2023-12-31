package com.example.mynewhumblr.ui.subreddits_fragment

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.SUBSCRIBE
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.UNSUBSCRIBE
import com.example.mynewhumblr.data.models.PostModel
import com.example.mynewhumblr.data.models.SubredditModel
import com.example.mynewhumblr.databinding.ItemPostImageBinding
import com.example.mynewhumblr.databinding.ItemSubredditBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateViewBindingViewHolder
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun subredditsDelegate(
    onClick: (subQuery: SubQuery, item: ListItem, clickableView: ClickableView) -> Unit,
) = adapterDelegateViewBinding<SubredditModel, ListItem, ItemSubredditBinding>(
    { inflater, root -> ItemSubredditBinding.inflate(inflater, root, false) }
) {
    bind {
        binding.subredditTitle.text = item.namePrefixed
        binding.subscribeButton.isSelected = item.isUserSubscriber == true
        if (item.imageUrl != null) {
            Glide.with(binding.fullSubredditCard)
                .load(item.imageUrl!!)
                .circleCrop()
                .error(R.drawable.ic_r_humblr)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .into(binding.subredditImage)
        }
    }
    binding.subscribeButton.setOnClickListener {
        binding.subscribeButton.isSelected = !binding.subscribeButton.isSelected
        val action = if (!binding.subscribeButton.isSelected) UNSUBSCRIBE else SUBSCRIBE
        onClick(SubQuery(name = item.name, action = action), item, ClickableView.SUBSCRIBE)
    }

    binding.fullSubredditCard.setOnClickListener {
        onClick(SubQuery(id = item.id), item, ClickableView.SUBREDDIT)
    }
}

fun postsDelegate(
    onClick: (subQuery: SubQuery, item: ListItem, clickableView: ClickableView) -> Unit,
) = adapterDelegateViewBinding<PostModel, ListItem, ItemPostImageBinding>(
    { inflater, root -> ItemPostImageBinding.inflate(inflater, root, false) }
) {
    bind {
        showScore(item.score)
        binding.postTitle.text = item.title
        binding.subredditName.text = item.subredditNamePrefixed
        binding.userName.text = context.getString(R.string.author, item.author)
        if (item.postHint == "image") {
            binding.postBodyImage.apply {
                Glide.with(this)
                    .load(item.url)
                    .error(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .into(this)
                visibility = View.VISIBLE
            }
        } else binding.postBodyImage.visibility = View.GONE

        binding.upVoteButton.isSelected = item.likedByUser == true

        binding.upVoteButton.setOnClickListener {
            if (!binding.upVoteButton.isSelected) {
                onClick(SubQuery(voteDirection = 1, name = item.name), item, ClickableView.VOTE)
                showScore(item.score + 1)
            } else {
                onClick(SubQuery(voteDirection = 0, name = item.name), item, ClickableView.VOTE)
                showScore(item.score)
            }
            binding.upVoteButton.isSelected = !binding.upVoteButton.isSelected
            binding.downVoteButton.isSelected = false
        }

        binding.downVoteButton.isSelected = item.likedByUser == false
        binding.downVoteButton.setOnClickListener {
            if (!binding.downVoteButton.isSelected) {
                onClick(SubQuery(voteDirection = -1, name = item.name), item, ClickableView.VOTE)
                showScore(item.score - 1)
            } else {
                onClick(SubQuery(voteDirection = 0, name = item.name), item, ClickableView.VOTE)
                showScore(item.score)
            }
            binding.downVoteButton.isSelected = !binding.downVoteButton.isSelected
            binding.upVoteButton.isSelected = false
        }

        binding.saveButton.isSelected = item.saved == true
        binding.saveButton.setOnClickListener {
            if (binding.saveButton.isSelected) {
                Snackbar.make(
                    binding.root, getString(R.string.unsaved),
                    BaseTransientBottomBar.LENGTH_SHORT
                ).show()
                onClick(SubQuery(name = item.name), item, ClickableView.UNSAVE)
            } else {
                Snackbar.make(
                    binding.root, getString(R.string.saved),
                    BaseTransientBottomBar.LENGTH_SHORT
                )
                    .show()
                onClick(SubQuery(name = item.name), item, ClickableView.SAVE)
            }
            binding.saveButton.isSelected = !binding.saveButton.isSelected
        }

        binding.userName.setOnClickListener {
            onClick(SubQuery(name = item.author), item, ClickableView.USER)
        }

    }
    binding.subredditCard.setOnClickListener {
        onClick(SubQuery(id = item.id), item, ClickableView.SUBREDDIT)
    }
}

private fun AdapterDelegateViewBindingViewHolder<PostModel, ItemPostImageBinding>.showScore(score: Int) {
    if (score > 999_999) {
        binding.likes.text = getString(R.string.likesM, score / 1_000_000)
    } else {
        if (score > 999) binding.likes.text = getString(R.string.likesK, score / 1_000)
        else binding.likes.text = score.toString()
    }
}