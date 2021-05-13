package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.databinding.ItemSearchResultsBinding
import zzz.bing.himalaya.utils.getImageUrl

class SearchResultsAdapter : ListAdapter<Album, SearchResultsAdapter.SearchResultsViewHolder>(
    object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem.equals(newItem)
    }
) {
    private var itemClickEvent: ((item: Album) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val binding =
            ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SearchResultsViewHolder(binding)
        viewHolder.itemView.setOnClickListener {
            itemClickEvent?.also { it(getItem(viewHolder.adapterPosition)) }
        }
        return viewHolder
    }

    /**
     * 外部设置事件处理
     * @param block Function1<Album -> Unit>
     */
    fun setItemClickEvent(block: (item: Album) -> Unit) {
        itemClickEvent = block
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        binding.textSearchResultsTitle.text = item.albumTitle
        binding.textSearchResultsInfo.text = item.albumIntro
        binding.textTotalCount.text = item.includeTrackCount.toString()
        binding.textPlayCount.text = getPlayCount(item.playCount)

        item.getImageUrl()?.also { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .into(binding.imageItemIcon)
        }
    }

    /**
     * 数字转换为以万为单位的字符串
     * @param playCount Long
     * @return String
     */
    private fun getPlayCount(playCount: Long) =
        if (playCount > 10_000) {
            val integer = playCount / 10_000
            val decimal = (playCount % 10_000) / 1000
            if (decimal == 0L) {
                "${integer}万"
            } else {
                "$integer.${decimal}万"
            }
        } else {
            playCount.toString()
        }

    inner class SearchResultsViewHolder(val binding: ItemSearchResultsBinding) :
        RecyclerView.ViewHolder(binding.root)
}