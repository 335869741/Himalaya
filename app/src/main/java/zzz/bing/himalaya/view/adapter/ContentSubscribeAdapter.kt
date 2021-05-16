package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zzz.bing.himalaya.databinding.ItemContentSubscribeBinding
import zzz.bing.himalaya.db.entity.AlbumSubscribe

class ContentSubscribeAdapter :
    PagingDataAdapter<AlbumSubscribe, ContentSubscribeAdapter.ContentHomeViewHolder>(
        object : DiffUtil.ItemCallback<AlbumSubscribe>() {
            override fun areItemsTheSame(oldItem: AlbumSubscribe, newItem: AlbumSubscribe) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AlbumSubscribe, newItem: AlbumSubscribe) =
                oldItem.id == newItem.id
        }
    ) {

    private var mLongPressEvent: ((AlbumSubscribe?) -> Unit)? = null
    private var mClickEvent: ((AlbumSubscribe?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHomeViewHolder {
        val binding = ItemContentSubscribeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ContentHomeViewHolder(binding)
        setListener(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ContentHomeViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        binding.textItemTitle.text = item?.title
        binding.textItemContent.text = item?.info
        binding.textItemContentCount.text = item?.trackCount.toString()
        binding.textPlayCount.text = getPlayCount(item?.playCount!!)

        item.coverUrl?.also { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .into(binding.imageItemIcon)
        }
    }

    /**
     * 设置监听器
     * @param holder ContentHomeViewHolder
     */
    private fun setListener(holder: ContentHomeViewHolder) {
        holder.itemView.setOnClickListener {
            mClickEvent?.also { it(getItem(holder.bindingAdapterPosition)) }
        }
        holder.itemView.setOnLongClickListener {
            mLongPressEvent?.also { it(getItem(holder.bindingAdapterPosition)) }
            false
        }
    }

    /**
     * 点击事件
     * @param block Function1<AlbumSubscribe? -> Unit>
     */
    fun setClickEvent(block: (AlbumSubscribe?) -> Unit) {
        mClickEvent = block
    }

    /**
     * 长按事件
     * @param block Function1<AlbumSubscribe? -> Unit>
     */
    fun setLongPressEvent(block: (AlbumSubscribe?) -> Unit) {
        mLongPressEvent = block
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