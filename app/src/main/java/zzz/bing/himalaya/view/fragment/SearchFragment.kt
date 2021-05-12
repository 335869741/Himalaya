package zzz.bing.himalaya.view.fragment

import android.content.Context
import android.view.*
import android.view.View.generateViewId
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.word.HotWord
import com.ximalaya.ting.android.opensdk.model.word.QueryResult
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentSearchBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.SizeUtils
import zzz.bing.himalaya.view.adapter.SearchLenovoAdapter
import zzz.bing.himalaya.view.adapter.SearchResultsAdapter
import zzz.bing.himalaya.viewmodel.SearchViewModel
import zzz.bing.himalaya.views.UILoader
import java.util.*

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private lateinit var mSearchLenovoAdapter: SearchLenovoAdapter
    private lateinit var mSearchResultsAdapter: SearchResultsAdapter
    private lateinit var mSearchView: SearchView
    private lateinit var myUILoad: MyUILoad

    private var searchString: String? = null

    override fun initViewModel() = ViewModelProvider(this).get(SearchViewModel::class.java)

    override fun initViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun initView() {
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            title = ""
        }
        mSearchLenovoAdapter = SearchLenovoAdapter()
        binding.frameSearchLenovo.adapter = mSearchLenovoAdapter
        binding.frameSearchLenovo.layoutManager = LinearLayoutManager(requireContext())

        mSearchResultsAdapter = SearchResultsAdapter()
        myUILoad = MyUILoad(requireContext())
        binding.frameSearchResults.addView(myUILoad)
        myUILoad.recycler.layoutManager = LinearLayoutManager(requireContext())
        myUILoad.recycler.adapter = mSearchResultsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        mSearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        mSearchView.maxWidth = binding.root.width
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.also {
                    viewModel.getSearchAlbums(it)
                    searchString = it
                    hiddenView()
                    binding.frameSearchResults.visibility = View.VISIBLE
                    myUILoad.setNoMoreData()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                this@SearchFragment.onQueryTextChange(newText)
                return false
            }
        })
        mSearchView.setOnCloseListener {
            hiddenView()
            binding.linearSearchKey.visibility = View.VISIBLE
            false
        }
    }

    /**
     * 查询词变化的监听
     * @param newText String?
     */
    private fun onQueryTextChange(newText: String?) {
        if (!newText.isNullOrBlank()) {
            viewModel.queryTextLenovo(newText)
        } else {
            hiddenView()
            binding.linearSearchKey.visibility = View.VISIBLE
        }
    }

    override fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mSearchLenovoAdapter.setOnClickCallBack { keyWord ->
            mSearchView.setQuery(keyWord, true)
        }
    }

    override fun initData() {
        viewModel.getHotsWords()
        viewModel.pageClear()
    }


    override fun initObserver() {
        viewModel.hotSearch.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                getHotWords(it)
            }
        }
        viewModel.searchLenovo.observe(viewLifecycleOwner) {
            searchLenovoCallBack(it)
            LogUtils.d(this, "list ==> $it")
        }
        viewModel.searchResults.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                searchResults(it)
            }
        }
    }

    /**
     * 搜索结果回调
     * @param albums List<Album>
     */
    private fun searchResults(albums: List<Album>) {
        mSearchResultsAdapter.submitList(albums)
        LogUtils.d(this, "searchResults")
    }

    /**
     * 联想词的回调
     * @param list List<QueryResult>?
     */
    private fun searchLenovoCallBack(list: List<QueryResult>?) {
        if (!list.isNullOrEmpty() && !binding.frameSearchResults.isVisible) {
            hiddenView()
            binding.frameSearchLenovo.visibility = View.VISIBLE
            mSearchLenovoAdapter.submitList(list)
        }
    }

    /**
     * 热词结果回调
     * @param list List<HotWord>
     */
    private fun getHotWords(list: List<HotWord>) {
        addWords(binding.constraintHot, list)
    }

    /**
     * 给constraint布局设置flow形式text
     * @param constraint ConstraintLayout
     * @param list List<HotWord>
     */
    private fun addWords(constraint: ConstraintLayout, list: List<HotWord>) {
        val flow = newFlow()
        if (constraint.childCount > 0) {
            constraint.removeAllViews()
        }
        constraint.addView(flow)
        val ids = ArrayList<Int>()
        list.forEachIndexed { index, hotWord ->
            constraint.addView(
                TextView(requireContext()).apply {
                    text = hotWord.searchword
                    val randomId = generateViewId()
                    id = randomId
                    ids.add(randomId)
                    setPadding(SizeUtils.dip2px(3f))
                    isClickable = true
                    background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.selector_flow_text)
                    maxLines = 1
                    setOnClickListener {
                        flowTextClick(it as TextView, index)
                    }
                }
            )
        }
        flow.referencedIds = ids.toIntArray()
    }

    /**
     * flowText的点击事件
     * @param textView TextView
     */
    private fun flowTextClick(textView: TextView, index: Int) {
        mSearchView.setQuery(textView.text, true)
        mSearchView.isIconified = false
        LogUtils.i(this, "text == ${textView.text} || index ==> $index")
    }

    /**
     * 创建一个flow并初始化
     * @return Flow
     */
    private fun newFlow(): Flow {
        return Flow(requireContext()).apply {
            setOrientation(Flow.HORIZONTAL)
            setHorizontalGap(SizeUtils.dip2px(4f))
            setVerticalGap(SizeUtils.dip2px(4f))
            setWrapMode(Flow.WRAP_CHAIN)
        }
    }

    /**
     * 隐藏view
     */
    private fun hiddenView() {
        binding.frameSearchLenovo.visibility = View.GONE
        binding.linearSearchKey.visibility = View.GONE
        binding.frameSearchResults.visibility = View.GONE
    }

    private fun loadMoreEvent() {
        searchString?.also {
            viewModel.getSearchAlbums(it)
        }
    }

    inner class MyUILoad(context: Context) : UILoader(context) {

        lateinit var recycler: RecyclerView

        private val isLoadMore get() = (success as RefreshLayout).state == RefreshState.Loading

        override fun getSuccessView() = SmartRefreshLayout(context).apply {
            recycler = RecyclerView(context).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                itemAnimator?.apply { changeDuration = 0 }
            }
            setRefreshFooter(ClassicsFooter(context))
            setRefreshContent(recycler)
            setEnableRefresh(false)
            setOnLoadMoreListener { loadMoreEvent() }
        }

        /**
         *  加载完成
         * @param uiStatus UIStatus
         */
        override fun uiStatusChange(uiStatus: UIStatus) {
            if (isLoadMore && uiStatus == UIStatus.SUCCESS) {
                (success as RefreshLayout).finishLoadMore()
            }
        }

        /**
         * 加载失败，没有更多数据
         */
        override fun loadMoreEmpty() {
            (success as RefreshLayout).finishLoadMoreWithNoMoreData()
        }

        /**
         * 加载失败，网络错误
         */
        override fun loadMoreError() {
            (success as RefreshLayout).finishLoadMore(false)
        }

        override fun getUIStatusLiveData() = viewModel.netState

        override fun getLifecycleOwner() = viewLifecycleOwner

        /**
         * 恢复没有更多数据的原始状态
         */
        fun setNoMoreData() {
            (success as RefreshLayout).setNoMoreData(false)
        }
    }
}