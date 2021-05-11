package zzz.bing.himalaya.view.fragment

import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.generateViewId
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ximalaya.ting.android.opensdk.model.word.HotWord
import com.ximalaya.ting.android.opensdk.model.word.QueryResult
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentSearchBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.SizeUtils
import zzz.bing.himalaya.view.adapter.SearchLenovoAdapter
import zzz.bing.himalaya.viewmodel.SearchViewModel
import java.util.*

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private lateinit var mSearchLenovoAdapter: SearchLenovoAdapter
    private lateinit var mSearchView: SearchView

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        mSearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        mSearchView.maxWidth = binding.root.width
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                TODO("Not yet implemented")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                this@SearchFragment.onQueryTextChange(newText)
                return false
            }
        })
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
    }

    override fun initData() {
        viewModel.getHotsWords()
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
    }

    /**
     * 联想词的回调
     * @param list List<QueryResult>?
     */
    private fun searchLenovoCallBack(list: List<QueryResult>?) {
        hiddenView()
        if (list.isNullOrEmpty()) {
            binding.linearSearchKey.visibility = View.VISIBLE
        } else {
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
        LogUtils.d(this, "text == ${textView.text} || index ==> $index")
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

    private fun hiddenView() {
        binding.frameSearchLenovo.visibility = View.GONE
        binding.linearSearchKey.visibility = View.GONE
        binding.frameSearchResults.visibility = View.GONE
    }
}