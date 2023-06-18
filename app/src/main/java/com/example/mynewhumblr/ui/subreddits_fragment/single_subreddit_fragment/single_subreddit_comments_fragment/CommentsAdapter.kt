package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment.single_subreddit_comments_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.Comments
import com.example.mynewhumblr.databinding.ItemCommentBinding

class CommentsAdapter(
    private val onAuthorClick: (String) -> Unit
) : PagingDataAdapter<Comments.Data.Children, CommentsViewHolder>(DiffUtilCallbackChildren()) {
    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            item?.let {
                Glide
                    .with(imageViewCommenterAvatar.context)
                    .load(it.data.iconImg)
                    .placeholder(R.drawable.ic_r_humblr)
                    .into(imageViewCommenterAvatar)
                textViewCommentAuthor.text = it.data.author
                textViewComment.text = it.data.body
                textViewCommentAuthor.setOnClickListener {
                    onAuthorClick.invoke(item.data.author.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

class CommentsViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

class DiffUtilCallbackChildren : DiffUtil.ItemCallback<Comments.Data.Children>() {
    override fun areItemsTheSame(oldItem: Comments.Data.Children, newItem: Comments.Data.Children): Boolean =
        oldItem.data.id == newItem.data.id

    override fun areContentsTheSame(oldItem: Comments.Data.Children, newItem: Comments.Data.Children): Boolean =
        oldItem == newItem
}