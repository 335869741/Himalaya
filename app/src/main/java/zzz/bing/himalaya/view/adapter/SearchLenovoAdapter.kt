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
    private var mCallBack: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLenovoViewHolder {
        val binding =
            ItemSearchLenovoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SearchLenovoViewHolder(binding)
        viewHolder.itemView.setOnClickListener {
            onClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    /**
     * 点击事件
     * @param position Int
     * @return Unit
     */
    private fun onClick(position: Int) {
        LogUtils.d(this, "index ==> $position")
        mCallBack?.also { it(getItem(position).keyword) }
    }

    /**
     * 设置点击事件回调
     * @param callBack Function2<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] QueryResult, Unit>
     */
    fun setOnClickCallBack(callBack: ((keyWord: String) -> Unit)) {
        mCallBack = callBack
    }

    override fun onBindViewHolder(holder: SearchLenovoViewHolder, position: Int) {
        holder.binding.textLenovo.text = getItem(position).keyword
    }

    inner class SearchLenovoViewHolder(val binding: ItemSearchLenovoBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}