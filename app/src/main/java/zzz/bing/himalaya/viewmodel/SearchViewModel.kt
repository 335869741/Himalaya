package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList
import com.ximalaya.ting.android.opensdk.model.word.HotWord
import com.ximalaya.ting.android.opensdk.model.word.HotWordList
import com.ximalaya.ting.android.opensdk.model.word.QueryResult
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.views.UILoader

class SearchViewModel : ViewModel() {

    private var mSearchPage = 1
    private var mSearchKeyword = StringBuilder()

    private val mHotSearch by lazy { MutableLiveData<List<HotWord>>() }
    private val mSearchLenovo by lazy { MutableLiveData<List<QueryResult>>() }
    private val mSearchResults by lazy { MutableLiveData<List<Album>>() }

    val netState by lazy { MutableLiveData<UILoader.UIStatus>() }
    val hotSearch: LiveData<List<HotWord>> get() = mHotSearch
    val searchLenovo: LiveData<List<QueryResult>> get() = mSearchLenovo
    val searchResults: LiveData<List<Album>> get() = mSearchResults

    /**
     * 获得热词
     */
    fun getHotsWords() {
        CommonRequest.getHotWords(
            mapOf<String, String>(DTransferConstants.TOP to "20"),
            object : IDataCallBack<HotWordList> {
                override fun onSuccess(p0: HotWordList?) {
                    p0?.also {
                        mHotSearch.postValue(it.hotWordList)
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                    LogUtils.e(
                        this@SearchViewModel,
                        "error code ==>$p0 | error message ==>$p1"
                    )
                    viewModelScope.launch {
                        delay(1000)
                        getHotsWords()
                    }
                }
            })
    }

    /**
     * 提交联想搜索
     * @param text String
     */
    fun queryTextLenovo(text: String) {
        CommonRequest.getSuggestWord(mapOf<String, String>(DTransferConstants.SEARCH_KEY to text),
            object : IDataCallBack<SuggestWords> {
                override fun onSuccess(p0: SuggestWords?) {
                    if (p0 == null) {
                        mSearchLenovo.postValue(null)
                    } else {
                        mSearchLenovo.postValue(p0.keyWordList)
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                    LogUtils.e(
                        this@SearchViewModel,
                        "error code ==>$p0 | error message ==>$p1"
                    )
                }
            }
        )
    }

    /**
     *
     * @param keyWord String
     */
    fun getSearchAlbums(keyWord: String) {
        val page = getSearchPage(keyWord)
        if (mSearchPage == 1) {
            netState.value = UILoader.UIStatus.LOADING
        } else {
            netState.value = UILoader.UIStatus.LOAD_MORE
        }
        CommonRequest.getSearchedAlbums(
            mapOf<String, String>(
                DTransferConstants.SEARCH_KEY to keyWord,
                DTransferConstants.PAGE to page
            ), object : IDataCallBack<SearchAlbumList> {
                override fun onSuccess(p0: SearchAlbumList?) {
                    if (p0 == null || p0.albums.isNullOrEmpty()) {
                        LogUtils.w(this@SearchViewModel, "onSuccess NullOrEmpty !!!")
                    netState.postValue(UILoader.UIStatus.EMPTY)
                } else {
                    if (mSearchPage > 1) {
                        mSearchResults.postValue(
                            ArrayList<Album>().apply {
                                addAll(mSearchResults.value ?: emptyList())
                                addAll(p0.albums)
                            }
                        )
                    } else {
                        mSearchResults.postValue(p0.albums)
                    }
                        netState.postValue(UILoader.UIStatus.SUCCESS)
                    }
                    LogUtils.d(
                        this@SearchViewModel,
                        """
                    netState ==> ${netState.value} 
                    mSearchPage ==> $mSearchPage 
                    mSearchResults ==> ${mSearchResults.value}
                    """.trimMargin()
                    )
                }

            override fun onError(p0: Int, p1: String?) {
                LogUtils.e(this@SearchViewModel, "error code ==>$p0 | error message ==>$p1")
                netState.postValue(UILoader.UIStatus.NETWORK_ERROR)
            }
        })
    }

    /**
     * 获取页数
     * @param keyWord String
     */
    private fun getSearchPage(keyWord: String): String {
        if (keyWord == mSearchKeyword.toString()) {
            mSearchPage++
        } else {
            mSearchKeyword.setLength(0)
            mSearchKeyword.append(keyWord)
            mSearchPage = 1
        }
        LogUtils.i(
            this,
            """
            mSearchPage ==> $mSearchPage 
            keyWord ==> $keyWord 
            mSearchKeyword ==> $mSearchKeyword
            """.trimMargin()
        )
        return mSearchPage.toString()
    }

    /**
     *
     */
    fun pageClear() {
        mSearchKeyword.setLength(0)
        mSearchPage = 1
    }
}