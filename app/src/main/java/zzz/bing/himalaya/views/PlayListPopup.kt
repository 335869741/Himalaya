package zzz.bing.himalaya.views

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import zzz.bing.himalaya.BaseApplication
import zzz.bing.himalaya.R

class PlayListPopup(height: Int, view: View) :
    PopupWindow(
        LayoutInflater.from(BaseApplication.getContext())
            .inflate(R.layout.popup_play_list, null),
        ViewGroup.LayoutParams.MATCH_PARENT,
        height
    ) {

    init {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
    }

    private val delay = 300L

    private val popAnime by lazy {
        ObjectAnimator.ofFloat(contentView, "y", 0f).also { it.duration = delay }
    }
    private val backgroundAnime by lazy {
        ObjectAnimator.ofFloat(view, "alpha", 1f).also { it.duration = delay }
    }

    val recycler: RecyclerView by lazy { contentView.findViewById(R.id.recycler) }
    val textExit: TextView by lazy { contentView.findViewById(R.id.textBottom) }
    val textPlayMode: TextView by lazy { contentView.findViewById(R.id.textPlayMode) }
    val textPlayOrder: TextView by lazy { contentView.findViewById(R.id.textPlayOrder) }
    val imagePlayMode: ImageView by lazy { contentView.findViewById(R.id.imagePlayMode) }
    val imagePlayOrder: ImageView by lazy { contentView.findViewById(R.id.imagePlayOrder) }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        if (!popAnime.isRunning) {
            popAnime.setFloatValues(height.toFloat(), 0f)
            backgroundAnime.setFloatValues(1f, 0.7f)
            popAnime.start()
            backgroundAnime.start()
        }
    }

    override fun dismiss() {
        popAnime.setFloatValues(0f, height.toFloat())
        backgroundAnime.setFloatValues(0.7f, 1f)
        popAnime.start()
        backgroundAnime.start()
        runBlocking {
            GlobalScope.launch {
                delay(delay)
                withContext(Dispatchers.Main) {
                    super.dismiss()
                }
            }
        }
    }

    /**
     * 直接关闭，不播放动画
     */
    fun immediateDismiss() {
        super.dismiss()
    }
}