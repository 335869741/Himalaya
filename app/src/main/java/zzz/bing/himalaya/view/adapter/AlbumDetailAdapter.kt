package zzz.bing.himalaya.view.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemAlbumDetailBinding
import zzz.bing.himalaya.view.MainActivity
import zzz.bing.himalaya.view.fragment.AlbumDetailFragment
import java.util.*

class AlbumDetailAdapter(private val albumDetailFragment: AlbumDetailFragment) :
    ListAdapter<Track, AlbumDetailAdapter.DetailViewHolder>(
        object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.dataId == newItem.dataId
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.dataId == newItem.dataId
            }
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding =
            ItemAlbumDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val detailViewHolder = DetailViewHolder(binding)
        detailViewHolder.itemView.setOnClickListener { itemView ->
            albumDetailFragment.findNavController().also { navController ->
                navController.navigate(R.id.action_detailFragment_to_playerFragment)
                val main = albumDetailFragment.requireActivity() as MainActivity
                main.getViewModel().playList.postValue(currentList)
                main.getViewModel().playPosition.postValue(detailViewHolder.adapterPosition)
            }
        }
        return detailViewHolder
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        binding.textItemPlayCount.text = getPlayCount(item.playCount.toLong())
        binding.textItemTitle.text = item.trackTitle
        binding.textItemTime.text = getTime(item.duration)
        binding.textItemDate.text =
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(item.createdAt)
        binding.textCount.text = position.toString()
    }

    inner class DetailViewHolder(val binding: ItemAlbumDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

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

    /**
     * 时间转换
     * @param time Int
     * @return String
     */
    private fun getTime(time: Int): String {
        return if (time < 60) {
            "00:${makeup(time)}"
        } else {
            val sec = time % 60
            val minute = time / 60
            "${makeup(minute)}:${makeup(sec)}"
        }
    }

    /**
     * 数字转换
     * @param number Int
     * @return String
     */
    private fun makeup(number: Int): String {
        return if (number < 9) {
            "0$number"
        } else {
            number.toString()
        }
    }
}