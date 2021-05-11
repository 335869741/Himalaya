package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.word.HotWord
import com.ximalaya.ting.android.opensdk.model.word.HotWordList
import com.ximalaya.ting.android.opensdk.model.word.QueryResult
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zzz.bing.himalaya.utils.LogUtils

class SearchViewModel : ViewModel() {

    private val mHotSearch by lazy { MutableLiveData<List<HotWord>>() }
    private val mSearchLenovo by lazy { MutableLiveData<List<QueryResult>>() }

    val hotSearch: LiveData<List<HotWord>> get() = mHotSearch
    val searchLenovo: LiveData<List<QueryResult>> get() = mSearchLenovo

    fun getHotsWords() {
        CommonRequest.getHotWords(mapOf<String, String>(DTransferConstants.TOP to "20"),
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


}