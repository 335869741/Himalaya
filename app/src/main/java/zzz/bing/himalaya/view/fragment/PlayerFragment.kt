package zzz.bing.himalaya.view.fragment

import android.icu.text.Transliterator
import android.view.View
import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentPlayerBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.view.MainActivity
import zzz.bing.himalaya.view.adapter.PlayerAdapter
import zzz.bing.himalaya.utils.putAll
import zzz.bing.himalaya.viewmodel.PlayerViewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding, PlayerViewModel>() {
    private lateinit var mPlayerAdapter : PlayerAdapter

    private val mMainActivity by lazy { requireActivity() as MainActivity }

    override fun initViewModel() = ViewModelProvider(this).get(PlayerViewModel::class.java)

    override fun initViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)

    override fun initView() {
        mPlayerAdapter =  PlayerAdapter(requireActivity())
        binding.pager.adapter = mPlayerAdapter
    }

    override fun initObserver() {
        mMainActivity.getViewModel().playList.observe(viewLifecycleOwner) { trackList ->
//            mPlayerAdapter.playList.clear()
//            mPlayerAdapter.playList.addAll(trackList)
            mPlayerAdapter.playList.putAll(trackList)
        }
        mMainActivity.getViewModel().playPosition.observe(viewLifecycleOwner){ position ->
            binding.pager.setCurrentItem(position,false)
        }
    }

    override fun initListener() {
        binding.imagePlay.setOnClickListener { view ->
            playClick(view)
        }
        binding.imagePlayerList.setOnClickListener { view ->
            playListClick(view)
        }
        binding.imagePlayerNext.setOnClickListener { view ->
            playNextClick(view)
        }
        binding.imagePlayerPrevious.setOnClickListener { view ->
            playPreviousClick(view)
        }
        binding.imagePlayerSort.setOnClickListener {  view ->
            playSortClick(view)
        }
    }

    /**
     * 排序事件
     * @param view View
     */
    private fun playSortClick(view : View) {
//        TODO("Not yet implemented")
        LogUtils.d(this,"playSortClick")
    }

    /**
     * 上一首事件
     * @param view View
     */
    private fun playPreviousClick(view : View) {
//        TODO("Not yet implemented")
        LogUtils.d(this,"playPreviousClick")
    }

    /**
     * 下一首事件
     * @param view View
     */
    private fun playNextClick(view : View) {
//        TODO("Not yet implemented")
        LogUtils.d(this,"playNextClick")
    }

    /**
     * 播放列表事件
     * @param view View
     */
    private fun playListClick(view : View) {
//        TODO("Not yet implemented")
        LogUtils.d(this,"playListClick")
    }

    /**
     * 播放事件
     * @param view View
     */
    private fun playClick(view : View) {
//        TODO("Not yet implemented")
        LogUtils.d(this,"playClick")
    }
}