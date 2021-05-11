package zzz.bing.himalaya.view.fragment

import android.view.Menu
import android.view.MenuInflater
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentSearchBinding
import zzz.bing.himalaya.viewmodel.SearchViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    private lateinit var mSearchView: SearchView

    override fun initViewModel() = ViewModelProvider(this).get(SearchViewModel::class.java)

    override fun initViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

    override fun initView() {
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            title = ""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        mSearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                TODO("Not yet implemented")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                TODO("Not yet implemented")
                return false
            }
        })
    }

    override fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}