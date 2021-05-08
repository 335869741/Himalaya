package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.ItemPopupPlayListBinding

class PopupPlayListAdapter : ListAdapter<Track, PopupPlayListAdapter.PopupPlayListViewHolder>(
    object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.dataId == newItem.dataId
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.dataId == newItem.dataId
        }
    }
) {

    var position = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupPlayListViewHolder {
        val itemBinding =
            ItemPopupPlayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = PopupPlayListViewHolder(itemBinding)
        viewHolder.itemView.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PopupPlayListViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.textPlayName.text = item.trackTitle

        if (this.position == position) {
            binding.textPlayName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.main)
            )
            binding.textPlayName.textSize = 24f
            binding.imagePlayIcon.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.main
                )
            )
            binding.imagePlayIcon.visibility = View.VISIBLE
        } else {
            binding.textPlayName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.popupTextColor)
            )
            binding.textPlayName.textSize = 18f
            binding.imagePlayIcon.visibility = View.GONE
        }
    }

    inner class PopupPlayListViewHolder(val binding: ItemPopupPlayListBinding) :
        RecyclerView.ViewHolder(binding.root)
}