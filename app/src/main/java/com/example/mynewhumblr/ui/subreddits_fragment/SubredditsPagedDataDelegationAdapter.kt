package com.example.mynewhumblr.ui.subreddits_fragment

import com.example.mynewhumblr.data.ClickableView
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.ui.ListItemDiffUtil
import com.example.mynewhumblr.ui.utils.tools.PagedDataDelegationAdapter

class SubredditsPagedDataDelegationAdapter(
    private val onClick: (subQuery: SubQuery, item: ListItem, clickableView: ClickableView) -> Unit,
) : PagedDataDelegationAdapter<ListItem>(ListItemDiffUtil()) {
    init {
        delegatesManager.addDelegate(subredditsDelegate {
                subQuery: SubQuery, listItem: ListItem, clickableView: ClickableView ->
            onClick(subQuery, listItem, clickableView)
        }
        )
            .addDelegate(postsDelegate {
                    subQuery: SubQuery, listItem: ListItem, clickableView: ClickableView ->
                onClick(subQuery, listItem, clickableView)
            }
            )
    }
}