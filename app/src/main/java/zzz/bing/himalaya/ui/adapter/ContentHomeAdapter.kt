package zzz.bing.himalaya.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemContentHomeBinding
import zzz.bing.himalaya.ui.fragment.AlbumDetailFragment
import zzz.bing.himalaya.ui.fragment.ContentHomeFragment
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.doOnEnd
import zzz.bing.himalaya.utils.getImageUrl

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentHomeViewHolder {
        val binding = ItemContentHomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val contentHomeViewHolder = ContentHomeViewHolder(binding)
        contentHomeViewHolder.itemView.setOnClickListener {
            val item = getItem(contentHomeViewHolder.adapterPosition)
            item.getImageUrl()?.also {
                fragment.findNavController().navigate(
                    R.id.action_homeFragment_to_detailFragment,
                    Bundle().apply {
                        putString(AlbumDetailFragment.ACTION_COVER_IMAGE_URL, it)
                        putString(AlbumDetailFragment.ACTION_ALBUM_TITLE, item.albumTitle)
                        putString(AlbumDetailFragment.ACTION_AUTHOR, item.albumIntro)
                        putLong(AlbumDetailFragment.ACTION_ITEM_ID,item.id)
                    }, null,
                    FragmentNavigatorExtras(
                        binding.imageItemIcon to "${AlbumDetailFragment.TRANSITION_IMAGE_ICON}_${item.id}"
//                                binding.imageItemIcon to AlbumDetailFragment.TRANSITION_IMAGE_ICON
                    )
                )
            }
            fragment.onChangItemForId(contentHomeViewHolder.adapterPosition)
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

        item.getImageUrl()?.also { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .doOnEnd { fragment.startPostponedEnterTransition() }
                .into(binding.imageItemIcon)
        }
        onSetTransitionName(binding, item)
    }

    /**
     * 共享元素设置标签
     * @param binding ItemContentHomeBinding
     * @param item Album
     */
    private fun onSetTransitionName(binding: ItemContentHomeBinding, item: Album) {
        ViewCompat.setTransitionName(
            binding.imageItemIcon,
            "${AlbumDetailFragment.TRANSITION_IMAGE_ICON}_${item.id}"
        )
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