package zzz.bing.himalaya.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt


object ImageBlur {
    private fun renderScriptBlur(context: Context, source: Bitmap, radius: Int): Bitmap {
        //(1)
        val renderScript = RenderScript.create(context)
        LogUtils.i(this, "scale size:" + source.width + "*" + source.height)

        // Allocate memory for Renderscript to work with
        //(2)
        val input = Allocation.createFromBitmap(renderScript, source)
        val output = Allocation.createTyped(renderScript, input.type)
        //(3)
        // Load up an instance of the specific script that we want to use.
        val scriptIntrinsicBlur =
            ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        //(4)
        scriptIntrinsicBlur.setInput(input)
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius.toFloat())
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output)
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(source)
        //(8)
        renderScript.destroy()
        return source
    }

    private fun renderScriptBlur(context: Context, source: Bitmap, radius: Int, scale: Float): Bitmap {
        LogUtils.i(this, "origin size:" + source.width + "*" + source.height)
        val width = (source.width * scale).roundToInt()
        val height = (source.height * scale).roundToInt()
        val inputBmp = Bitmap.createScaledBitmap(source, width, height, false)
        val renderScript = RenderScript.create(context)
        LogUtils.i(this, "scale size:" + inputBmp.width + "*" + inputBmp.height)

        // Allocate memory for Renderscript to work with
        val input = Allocation.createFromBitmap(renderScript, inputBmp)
        val output = Allocation.createTyped(renderScript, input.type)

        // Load up an instance of the specific script that we want to use.
        val scriptIntrinsicBlur =
            ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setInput(input)

        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius.toFloat())

        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output)

        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp)
        renderScript.destroy()
        return inputBmp
    }
}