package zzz.bing.himalaya.utils

import android.widget.Toast
import zzz.bing.himalaya.BaseApplication

object ToastUtil {

    fun longTs(text: String) {
        Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_LONG).show()
    }

    fun shotTs(text: String) {
        Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT).show()
    }
}