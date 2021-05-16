package zzz.bing.himalaya.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import zzz.bing.himalaya.R

class DialogSubscribe @JvmOverloads constructor(
    context: Context,
    cancelable: Boolean = true,
    cancelListener: DialogInterface.OnCancelListener? = null
) : Dialog(context, cancelable, cancelListener) {

    private lateinit var textCancel: TextView
    private lateinit var textSubmit: TextView

    private var mCancelEvent: ((dialog: DialogSubscribe) -> Unit)? = null
    private var mSubmitEvent: ((dialog: DialogSubscribe) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_subscribe)
        textCancel = findViewById(R.id.textCancel)
        textSubmit = findViewById(R.id.textSubmit)
        textCancel.setOnClickListener { mCancelEvent?.also { it(this) } }
        textSubmit.setOnClickListener { mSubmitEvent?.also { it(this) } }
    }

    fun setCancelClickListener(block: (dialog: DialogSubscribe) -> Unit): DialogSubscribe {
        mCancelEvent = block
        return this
    }

    fun setSubmitClickListener(block: (dialog: DialogSubscribe) -> Unit): DialogSubscribe {
        mSubmitEvent = block
        return this
    }
}