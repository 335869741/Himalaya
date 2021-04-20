package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.category.Category
import com.ximalaya.ting.android.opensdk.model.category.CategoryList
import zzz.bing.himalaya.utils.LogUtil


class HomeViewModel : ViewModel() {

//    private val mCategories by lazy { MutableLiveData<List<Category>>() }
//
//    val categories : LiveData<List<Category>> by lazy { mCategories }
//
//    init {
//        CommonRequest.getCategories(HashMap(), object : IDataCallBack<CategoryList> {
//            override fun onSuccess(p0: CategoryList?) {
//                LogUtil.d(this@HomeViewModel, "onSuccess CategoryList ==> $p0 ")
//                mCategories.postValue(p0?.categories)
//            }
//
//            override fun onError(p0: Int, p1: String?) {
//                LogUtil.e(this@HomeViewModel, "onError code ==> $p0 | message ==> $p1")
//            }
//        })
//    }
}