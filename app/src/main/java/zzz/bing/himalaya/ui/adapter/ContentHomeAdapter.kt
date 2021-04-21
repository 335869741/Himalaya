package zzz.bing.himalaya.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemContentHomeBinding
import zzz.bing.himalaya.ui.fragment.ContentHomeFragment
import zzz.bing.himalaya.utils.UtilLog

//RecyclerView的ListAdapter有丰富的动画
class ContentHomeAdapter(val fragment: ContentHomeFragment) : ListAdapter<Album, ContentHomeAdapter.ContentHomeViewHolder>(
    object : DiffUtil.ItemCallback<Album>() {
        //判断对象是否一致，通常判断id
        override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

        //判断内容是否一致，这里仅判断id
        override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHomeViewHolder {
        val binding = ItemContentHomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val contentHomeViewHolder = ContentHomeViewHolder(binding)
        contentHomeViewHolder.itemView.setOnClickListener {
            if (! fragment.findNavController().popBackStack(R.id.detailFragment,false)){
                fragment.findNavController().navigate(R.id.detailFragment)
            }
        }
        return contentHomeViewHolder
    }

    override fun onBindViewHolder(holder: ContentHomeViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        binding.textItemTitle.text = item.albumTitle
        binding.textItemContent.text = item.albumIntro
        binding.textItemContentCount.text = item.includeTrackCount.toString()
        binding.textPlayCount.text = getPlayCount(item.playCount)

        val imageUrl = getImageUrl(item)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(binding.imageItemIcon)
        } else {
            binding.imageItemIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_launcher_background
                )
            )
        }


    }

    /**
     *
     * @param album Album
     * @return String?
     */
    private fun getImageUrl(album: Album): String? {
        return when {
            !album.coverUrlMiddle.isNullOrEmpty() -> {
                album.coverUrlMiddle
            }
            !album.coverUrlMiddle.isNullOrEmpty() -> {
                album.coverUrlMiddle
            }
            !album.coverUrlSmall.isNullOrEmpty() -> {
                album.coverUrlSmall
            }
            else -> {
                UtilLog.w(
                    this,
                    "ImageUrl is null"
                )
                null
            }
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