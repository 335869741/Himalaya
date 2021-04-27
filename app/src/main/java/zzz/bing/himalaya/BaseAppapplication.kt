package zzz.bing.himalaya

import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest.ITokenStateChange
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException
import com.ximalaya.ting.android.opensdk.util.BaseUtil
import com.ximalaya.ting.android.opensdk.util.Logger
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import zzz.bing.himalaya.view.MainActivity
import java.io.IOException

import zzz.bing.himalaya.utils.LogUtils

@Suppress("unused")
class BaseApplication : Application() {

    companion object {
        const val REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?"

        private var appContext: Context? = null
        fun getContext() = appContext!!

        /**
         * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
         */
        //    public static final String REDIRECT_URL =  "http://api.ximalaya.com";
        val REDIRECT_URL = if (DTransferConstants.isRelease)
            "http://api.ximalaya.com/openapi-collector-app/get_access_token"
        else "http://qf.test.ximalaya.com/access_token/callback"
    }

    override fun onCreate() {
        super.onCreate()
        appContext = baseContext

        LogUtils.isRelease = false

        if (BaseUtil.isMainProcess(this)) {
            val mp3 = getExternalFilesDir("mp3")!!.absolutePath
            LogUtils.i(this, "地址是  $mp3")
            val mXimalaya = CommonRequest.getInstanse()
            mXimalaya.useHttps = true
            if (DTransferConstants.isRelease) {
                val mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af"
                mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8")
                mXimalaya.setPackid("com.app.test.android")
                mXimalaya.init(this, mAppSecret, getDeviceInfoProvider(this))
            } else {
                val mAppSecret = "0a09d7093bff3d4947a5c4da0125972e"
                mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183")
//                mXimalaya.setPackid("com.ximalaya.qunfeng")
                mXimalaya.setPackid("zzz.bing.himalaya")
                mXimalaya.init(this, mAppSecret, getDeviceInfoProvider(this))
            }
            AccessTokenManager.getInstanse().init(this)
            if (AccessTokenManager.getInstanse().hasLogin()) {
                registerLoginTokenChangeListener(this)
            }

            // 下载sdk
            XmDownloadManager.Builder(this)
                .maxDownloadThread(3) // 最大的下载个数 默认为1 最大为3
                .maxSpaceSize(Long.MAX_VALUE) // 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                .connectionTimeOut(15000) // 下载时连接超时的时间 ,单位毫秒 默认 30000
                .readTimeOut(15000) // 下载时读取的超时时间 ,单位毫秒 默认 30000
                .fifo(false) // 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                .maxRetryCount(3) // 出错时重试的次数 默认2次
                .progressCallBackMaxTimeSpan(1000) //  进度条progress 更新的频率 默认是800
                .requestTracker(requestTracker) // 日志 可以打印下载信息
                .savePath(mp3) // 保存的地址 会检查这个地址是否有效
                .create()
        }
    }

    private fun getDeviceInfoProvider(context: Context): IDeviceInfoProvider {
        return object : DeviceInfoProviderDefault(context) {
            override fun oaid(): String {
                return "!!!这里要传入真正的oaid oaid 接入请访问 http://www.msa-alliance.cn/col.jsp?id=120"
            }
        }
    }

//    private fun unregisterLoginTokenChangeListener() {
//        CommonRequest.getInstanse().iTokenStateChange = null
//    }

    private fun registerLoginTokenChangeListener(context: Context) {
        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        CommonRequest.getInstanse().iTokenStateChange = object : ITokenStateChange {
            // 此接口表示token已经失效 ,
            override fun getTokenByRefreshSync(): Boolean {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().refreshToken)) {
                    try {
                        return refreshSync()
                    } catch (e: XimalayaException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun getTokenByRefreshAsync(): Boolean {
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().refreshToken)) {
                    try {
                        refresh()
                        return true
                    } catch (e: XimalayaException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun tokenLosted() {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    @Throws(XimalayaException::class)
    fun refresh() {
        val client = OkHttpClient().newBuilder()
            .followRedirects(false)
            .build()
        val builder = FormBody.Builder()
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token")
        builder.add(
            XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN,
            AccessTokenManager.getInstanse().tokenModel.refreshToken
        )
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().appKey)
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().deviceId)
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID)
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().packId)
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().uid)
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL)
        val body = builder.build()
        val request: Request = Request.Builder()
            .url("https://api.ximalaya.com/oauth2/refresh_token?")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                Logger.d("refresh", "refreshToken, request failed, error message = " + e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
//                val statusCode = response.code()
                val body1 = response.body()?.string()!!
                println("TingApplication.refreshSync  1  $body1")
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(body1)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (jsonObject != null) {
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(
                        jsonObject.optString("access_token"),
                        jsonObject.optString("refresh_token"),
                        jsonObject.optLong("expires_in"),
                        jsonObject
                            .optString("uid")
                    )
                }
            }
        })
    }

    @Throws(XimalayaException::class)
    fun refreshSync(): Boolean {
        val client = OkHttpClient().newBuilder()
            .followRedirects(false)
            .build()
        val builder = FormBody.Builder()
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token")
        builder.add(
            XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN,
            AccessTokenManager.getInstanse().tokenModel.refreshToken
        )
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().appKey)
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().deviceId)
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID)
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().packId)
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().uid)
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL)
        val body = builder.build()
        val request: Request = Request.Builder()
            .url(REFRESH_TOKEN_URL)
            .post(body)
            .build()
        try {
            val execute: Response = client.newCall(request).execute()
            if (execute.isSuccessful) {
                try {
                    val string: String = execute.body()?.string()!!
                    val jsonObject = JSONObject(string)
                    println("TingApplication.refreshSync  2  $string")
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(
                        jsonObject.optString("access_token"),
                        jsonObject.optString("refresh_token"),
                        jsonObject.optLong("expires_in"),
                        jsonObject
                            .optString("uid")
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    private val requestTracker: RequestTracker = object : RequestTracker {
        override fun onWaiting(params: RequestParams) {
            Logger.log("TingApplication : onWaiting $params")
        }

        override fun onStart(params: RequestParams) {
            Logger.log("TingApplication : onStart $params")
        }

        override fun onRequestCreated(request: UriRequest) {
            Logger.log("TingApplication : onRequestCreated $request")
        }

        override fun onSuccess(request: UriRequest, result: Any) {
            Logger.log("TingApplication : onSuccess $request   result = $result")
        }

        override fun onRemoved(request: UriRequest) {
            Logger.log("TingApplication : onRemoved $request")
        }

        override fun onCancelled(request: UriRequest) {
            Logger.log("TingApplication : onCanclelled $request")
        }

        override fun onError(request: UriRequest, ex: Throwable, isCallbackError: Boolean) {
            Logger.log("TingApplication : onError $request   ex = $ex   isCallbackError = $isCallbackError")
        }

        override fun onFinished(request: UriRequest) {
            Logger.log("TingApplication : onFinished $request")
        }
    }
}