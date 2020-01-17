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

    private var mOriginalLeftPadding = -1f
    private var mTextWidth = 0f
    private var mSuffix = ""

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SuffixEditText, 0, 0).apply {
            try {
                getString(R.styleable.SuffixEditText_suffix)?.let { mSuffix = it }
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

    private fun calculateSuffix() {
        mOriginalLeftPadding = compoundPaddingLeft.toFloat()
        var displayedText: String = text.toString()
        if (displayedText.isEmpty()) displayedText = hint.toString()
        val widths = FloatArray(displayedText.length)
        paint.getTextWidths(displayedText, widths)
        mTextWidth = 0f
        for (w in widths) {
            mTextWidth += w
        }
    }

    override fun onPreDraw(): Boolean {
        calculateSuffix()
        return super.onPreDraw()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            mSuffix, mOriginalLeftPadding + mTextWidth,
            getLineBounds(0, null).toFloat(),
            paint
        )
    }
}

