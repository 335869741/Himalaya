package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zzz.bing.himalaya.databinding.ItemContentSubscribeBinding
import zzz.bing.himalaya.db.entity.AlbumSubscribe

class ContentSubscribeAdapter :
    ListAdapter<AlbumSubscribe, ContentSubscribeAdapter.ContentHomeViewHolder>(
        object : DiffUtil.ItemCallback<AlbumSubscribe>() {
            override fun areItemsTheSame(oldItem: AlbumSubscribe, newItem: AlbumSubscribe) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AlbumSubscribe, newItem: AlbumSubscribe) =
                oldItem.id == newItem.id
        }
    ) {

    private var mBindingSetEvent: ((ItemContentSubscribeBinding) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHomeViewHolder {
        val binding = ItemContentSubscribeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        binding.also { mBindingSetEvent?.apply { this(it) } }

        return ContentHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentHomeViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        binding.textItemTitle.text = item.title
        binding.textItemContent.text = item.info
        binding.textItemContentCount.text = item.trackCount.toString()
        binding.textPlayCount.text = getPlayCount(item.playCount)

        item.coverUrl?.also { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .into(binding.imageItemIcon)
        }
    }

    /**
     * item绑定事件
     * @param block Function1<ItemContentSubscribeBinding, Unit>
     */
    fun setBindingEvent(block: (ItemContentSubscribeBinding) -> Unit) {
        mBindingSetEvent = block
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

    inner class ContentHomeViewHolder(val binding: ItemContentSubscribeBinding) :
        RecyclerView.ViewHolder(binding.root)
}