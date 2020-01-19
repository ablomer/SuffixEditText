package com.ablomer.suffixedittext

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


/**
 * @author Augusto A. Blomer
 */
class SuffixEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        const val AUTOFILL_TYPE_TEXT = 1
    }

    private var mOriginalLeftPadding = -1f
    private var mTextWidth = 0f
    private var mHintMode = false

    private var mSuffix = ""
    private var mHintSuffix = ""

    init {
        // Disable multi-line text
        inputType = AUTOFILL_TYPE_TEXT
        maxLines = 1

        context.obtainStyledAttributes(attrs, R.styleable.SuffixEditText, 0, 0).apply {
            try {
                getString(R.styleable.SuffixEditText_suffix)?.let { mSuffix = it }
                getString(R.styleable.SuffixEditText_hintSuffix)?.let { mHintSuffix = it }
            } finally {
                recycle()
            }
        }
    }

    var suffix: String
        get() = mSuffix
        set(suffix) {
            mSuffix = suffix
            invalidate()
        }

    var hintSuffix: String
        get() = mHintSuffix
        set(suffix) {
            mHintSuffix = suffix
            invalidate()
        }

    private fun calculateSuffix(text: String) {
        mOriginalLeftPadding = compoundPaddingLeft.toFloat()
        val widths = FloatArray(text.length)
        paint.getTextWidths(text, widths)
        mTextWidth = 0f
        for (w in widths) {
            mTextWidth += w
        }
    }

    override fun onPreDraw(): Boolean {
        mHintMode = text.isNullOrEmpty()

        if (mHintMode) {
            calculateSuffix(hint.toString())
        } else {
            calculateSuffix(text.toString())
        }

        return super.onPreDraw()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            if (mHintMode) mHintSuffix else mSuffix,
            mOriginalLeftPadding + mTextWidth,
            getLineBounds(0, null).toFloat(),
            paint
        )
    }
}
