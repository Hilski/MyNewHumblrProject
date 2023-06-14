package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment.user_fragment


import android.content.ContentResolver
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.models.PostListing
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.databinding.ItemPostImageBinding

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class UserAdapter: RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    var data: List<PostListing.PostListingData.Post> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    class MyViewHolder(val binding: ItemPostImageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.apply {

 //           showScore(item.score)
            binding.postTitle.text = item.data.title
            binding.subredditName.text = item.data.subreddit_name_prefixed
            binding.userName.text = item.data.author
            if (item.data.post_hint == "image") {
                binding.postBodyImage.apply {
                    Glide.with(this)
                        .load(item.data.url)
                        .error(R.drawable.ic_launcher_foreground)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .into(this)
                    visibility = View.VISIBLE
                }
            } else binding.postBodyImage.visibility = View.GONE

            binding.downloadButton.setOnClickListener {
                if (!binding.downloadButton.isSelected) {
                    binding.downloadButton.isSelected = true
                    Snackbar.make(
                        binding.root, "Загружается",
                        BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                } else Snackbar.make(
                    binding.root, "Загружается",
                    BaseTransientBottomBar.LENGTH_SHORT
                )
                    .show()
            }

 /*           binding.upVoteButton.isSelected = item.data.likes  == true

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
                    onClick(
                        SubQuery(voteDirection = -1, name = item.name),
                        item,
                        ClickableView.VOTE
                    )
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

  */
        }
    }
}