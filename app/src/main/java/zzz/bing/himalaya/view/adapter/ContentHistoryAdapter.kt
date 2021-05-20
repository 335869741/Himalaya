package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemContentHistoryBinding
import zzz.bing.himalaya.db.entity.AlbumHistory

class ContentHistoryAdapter :
    PagingDataAdapter<AlbumHistory, ContentHistoryAdapter.ContentHistoryViewHolder>(
        object : DiffUtil.ItemCallback<AlbumHistory>() {
            override fun areItemsTheSame(oldItem: AlbumHistory, newItem: AlbumHistory): Boolean {
                return oldItem.albumId == newItem.albumId
            }

            override fun areContentsTheSame(oldItem: AlbumHistory, newItem: AlbumHistory): Boolean {
                return oldItem.albumId == newItem.albumId
            }
        }
    ) {

    private var mItemClick: ((ContentHistoryViewHolder) -> Unit)? = null

    fun setItemClickEvent(block: (ContentHistoryViewHolder) -> Unit) {
        mItemClick = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHistoryViewHolder {
        val binding =
            ItemContentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ContentHistoryViewHolder(binding)
        viewHolder.itemView.setOnClickListener {
            mItemClick?.also { it(viewHolder) }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ContentHistoryViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        binding.textItemTitle.text = item?.title
        binding.textItemContent.text = item?.info
        val str = "${item?.playNiCount!! + 1}"
        binding.textPlayCount.text =
            holder.itemView.context.getString(R.string.textHistoryHint, str)

        item.coverUrl?.also { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .into(binding.imageItemIcon)
        }
    }

    inner class ContentHistoryViewHolder(val binding: ItemContentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}