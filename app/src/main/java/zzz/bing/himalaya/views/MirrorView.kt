package zzz.bing.himalaya.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class MirrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setWillNotDraw(true)
    }

    private var _substance: View? = null
    var substance: View?
        get() = _substance
        set(value) {
            _substance = value
            setWillNotDraw(value == null)
        }

    override fun onDraw(canvas: Canvas?) {
        _substance?.draw(canvas)
    }
}