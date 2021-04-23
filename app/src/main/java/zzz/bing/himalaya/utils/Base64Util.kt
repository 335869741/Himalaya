package zzz.bing.himalaya.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException

object Base64Util {

    /**
     * 转换base64到bitmap
     * @param base64Data String
     * @return Bitmap?
     */
    fun base64ToBitmap(base64Data: String): Bitmap? {
        val subIndex = base64Data.indexOf(",")
        var data = base64Data
        if (subIndex != -1){
            data = base64Data.substring(subIndex + 1)
//            LogUtil.d(this, "base64 ==> $data")
        }
        val bytes = Base64.decode(data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 转换bitmap到base64
     * @param bitmap Bitmap
     * @return String?
     */
    fun bitmapToBase64(bitmap: Bitmap): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            baos.flush()
            baos.close()
            val bitmapBytes: ByteArray = baos.toByteArray()
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }
}