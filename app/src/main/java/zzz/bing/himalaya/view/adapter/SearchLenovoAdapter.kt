package zzz.bing.himalaya.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ximalaya.ting.android.opensdk.model.word.QueryResult
import zzz.bing.himalaya.databinding.ItemSearchLenovoBinding
import zzz.bing.himalaya.utils.LogUtils

class SearchLenovoAdapter : ListAdapter<QueryResult, SearchLenovoAdapter.SearchLenovoViewHolder>(
    object : DiffUtil.ItemCallback<QueryResult>() {
        override fun areItemsTheSame(oldItem: QueryResult, newItem: QueryResult): Boolean {
            return oldItem.queryId == newItem.queryId
        }

        override fun areContentsTheSame(oldItem: QueryResult, newItem: QueryResult): Boolean {
            return oldItem.queryId == newItem.queryId
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLenovoViewHolder {
        val binding =
            ItemSearchLenovoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SearchLenovoViewHolder(binding)
        viewHolder.itemView.setOnClickListener {
            onClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    private fun onClick(adapterPosition: Int) {
        LogUtils.d(this, "index ==> $adapterPosition")
    }

    override fun onBindViewHolder(holder: SearchLenovoViewHolder, position: Int) {
        holder.binding.textLenovo.text = getItem(position).keyword
    }

    inner class SearchLenovoViewHolder(val binding: ItemSearchLenovoBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}