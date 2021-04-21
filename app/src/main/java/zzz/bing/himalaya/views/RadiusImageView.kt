package zzz.bing.himalaya.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.Path

class RadiusImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val roundRatio = 0.1f
    private val path = Path().apply {
        addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            roundRatio * width,
            roundRatio * height,
            Path.Direction.CW
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restore()
    }
}