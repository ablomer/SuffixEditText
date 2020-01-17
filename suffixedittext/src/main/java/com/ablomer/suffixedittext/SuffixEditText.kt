package com.ablomer.suffixedittext

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText


/**
 * @author Augusto A. Blomer
 */
class SuffixEditText : AppCompatEditText {

    private var mOriginalLeftPadding = -1f
    private var mTextWidth = 0f
    private var mSuffix = ""

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    } // TODO: Need constructors for views in Kotlin?

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    var suffix: String
        get() = mSuffix
        set(suffix) {
            mSuffix = suffix
            invalidate()
        }

    override fun onKeyPreIme(key_code: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) clearFocus()
        return super.onKeyPreIme(key_code, event)
    }

    override fun onEditorAction(actionCode: Int) {
        super.onEditorAction(actionCode)
        if (actionCode == EditorInfo.IME_ACTION_DONE) clearFocus()
    }

    private fun calculateSuffix() {
        mOriginalLeftPadding = getCompoundPaddingLeft().toFloat()
        var displayedText: String = getText().toString()
        if (displayedText.length == 0) displayedText = getHint().toString()
        val widths = FloatArray(displayedText.length)
        getPaint().getTextWidths(displayedText, widths)
        mTextWidth = 0f
        for (w in widths) {
            mTextWidth += w
        }
    }

    override fun onPreDraw(): Boolean {
        calculateSuffix()
        return super.onPreDraw()
    }

    override protected fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            mSuffix, mOriginalLeftPadding + mTextWidth,
            getLineBounds(0, null).toFloat(),
            getPaint()
        )
    }
}

