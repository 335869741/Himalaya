package zzz.bing.himalaya.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.databinding.ItemDetailBinding

class DetailAdapter : ListAdapter<Track, DetailAdapter.DetailViewHolder>(
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
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val detailViewHolder = DetailViewHolder(binding)
        detailViewHolder.itemView.setOnClickListener { itemView ->

        }
        return detailViewHolder
    }

    override fun getItemCount(): Int {
//        return super.getItemCount()
        return 10
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.binding
    }

    inner class DetailViewHolder(val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root)
}