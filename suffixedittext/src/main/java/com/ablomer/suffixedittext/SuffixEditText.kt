package com.ablomer.suffixedittext

import android.content.Context
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


/**
 * Extended EditText with suffix features
 *
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

    private lateinit var mSuffixPaint: TextPaint
    private var mHintMode = false

    private var mSuffix = ""
    private var mHintSuffix = ""
    private var mSuffixColor: Int? = null
    private var mHintSuffixColor: Int? = null

    init {
        // Disable multi-line text
        inputType = AUTOFILL_TYPE_TEXT
        maxLines = 1

        context.obtainStyledAttributes(attrs, R.styleable.SuffixEditText, 0, 0).apply {
            try {
                getString(R.styleable.SuffixEditText_suffix)?.let { mSuffix = it }
                getString(R.styleable.SuffixEditText_hintSuffix)?.let { mHintSuffix = it }

                val suffixColor = getColor(R.styleable.SuffixEditText_suffixColor, -1)

                if (suffixColor != -1)
                    mSuffixColor = suffixColor

                val hintSuffixColor = getColor(R.styleable.SuffixEditText_hintSuffixColor, -1)

                if (hintSuffixColor != -1)
                    mHintSuffixColor = hintSuffixColor

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

    val suffixColor: Int
        get() = mSuffixColor ?: currentTextColor

    val hintSuffixColor: Int
        get() = mHintSuffixColor ?: currentHintTextColor

    val textWithSuffix: String
        get() = text.toString() + mSuffix

    val hintWithSuffix: String
        get() = hint.toString() + mHintSuffix

    private fun calculateSuffix(text: String?) {
        if (text == null)
            return

        mOriginalLeftPadding = compoundPaddingLeft.toFloat()
        val widths = FloatArray(text.length)
        paint.getTextWidths(text, widths)
        mTextWidth = 0f
        for (w in widths) {
            mTextWidth += w
        }
    }

    override fun onPreDraw(): Boolean {
        mSuffixPaint = TextPaint(paint)
        mHintMode = text.isNullOrEmpty()

        if (mHintMode) {
            mHintSuffixColor?.let { mSuffixPaint.color = it }
            calculateSuffix(hint?.toString())
        } else {
            mSuffixColor?.let { mSuffixPaint.color = it }
            calculateSuffix(text?.toString())
        }

        return super.onPreDraw()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            if (mHintMode) mHintSuffix else mSuffix,
            mOriginalLeftPadding + mTextWidth,
            getLineBounds(0, null).toFloat(),
            mSuffixPaint
        )
    }
}
