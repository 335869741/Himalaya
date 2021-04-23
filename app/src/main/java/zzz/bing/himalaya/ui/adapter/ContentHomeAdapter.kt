package zzz.bing.himalaya.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemContentHomeBinding
import zzz.bing.himalaya.domain.Action
import zzz.bing.himalaya.ui.fragment.ContentHomeFragment
import zzz.bing.himalaya.utils.Base64Util
import zzz.bing.himalaya.utils.Extension.getImageUrl
import zzz.bing.himalaya.utils.LogUtils

//RecyclerView的ListAdapter有丰富的动画
class ContentHomeAdapter(val fragment: ContentHomeFragment) :
    ListAdapter<Album, ContentHomeAdapter.ContentHomeViewHolder>(
        object : DiffUtil.ItemCallback<Album>() {
            //判断对象是否一致，通常判断id
            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

            //判断内容是否一致，这里仅判断id
            override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                oldItem.id == newItem.id
        }
    ) {
    private var index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHomeViewHolder {
        val binding = ItemContentHomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val contentHomeViewHolder = ContentHomeViewHolder(binding)
        contentHomeViewHolder.itemView.setOnClickListener { itemView->
            val item = itemView.tag as Album
            Base64Util.bitmapToBase64(binding.imageItemIcon.drawable.toBitmap())?.also { base64 ->
                Action.toDetail(
                    fragment.findNavController(),
                    base64,
                    item.albumTitle,
                    item.albumIntro,
                    binding.imageItemIcon
                )
            }
        }
        LogUtils.d(this, "onCreateViewHolder ${index++}")
        return contentHomeViewHolder
    }

    override fun onBindViewHolder(holder: ContentHomeViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        holder.itemView.tag = item

        binding.textItemTitle.text = item.albumTitle
        binding.textItemContent.text = item.albumIntro
        binding.textItemContentCount.text = item.includeTrackCount.toString()
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


    inner class ContentHomeViewHolder(val binding: ItemContentHomeBinding) :
        RecyclerView.ViewHolder(binding.root)
}