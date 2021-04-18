package zzz.bing.himalaya

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.category.CategoryList
import zzz.bing.himalaya.utils.LogUtil


class MainActivity : AppCompatActivity() {

    companion object{
        /**
         * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
         */
        //    public static final String REDIRECT_URL =  "http://api.ximalaya.com";
        val REDIRECT_URL = if (DTransferConstants.isRelease)
            "http://api.ximalaya.com/openapi-collector-app/get_access_token"
        else "http://qf.test.ximalaya.com/access_token/callback"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        BaseApplication.getContext()

        val map = HashMap<String,String>()
        CommonRequest.getCategories(map, object : IDataCallBack<CategoryList> {
            override fun onSuccess(p0: CategoryList?) {
                if (p0 != null){
                    LogUtil.d(this@MainActivity,"CategoryList size => ${p0.categories.size}")
                    LogUtil.d(this@MainActivity, "CategoryList all => ${p0.categories}")
                }else{
                    LogUtil.d(this@MainActivity,"CategoryList is null")
                }
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtil.e(this@MainActivity,"onError code ==> $p0 | onError message ==> $p1")
            }
        })

    }


}