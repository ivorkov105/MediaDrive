package com.example.mediadrive.ui.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mediadrive.R
import androidx.core.graphics.toColorInt
import kotlin.math.min

class CustomAudioPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    companion object {
        private const val TITLE_HEIGHT_RATIO = 0.08f
        private const val AUTHOR_HEIGHT_RATIO = 0.06f
        private const val TIMELINE_TOP_MARGIN_RATIO = 0.02f
        private const val TIMELINE_HEIGHT_RATIO = 0.04f
        private const val CONTROLS_HEIGHT_RATIO = 0.15f
        private const val CORNER_RADIUS_DP = 16f
        private const val PADDING_DP = 16f
    }

    private var albumArt: Bitmap? = null
    private var isPlaying: Boolean = false
    private var currentProgressRatio: Float = 0.0f
    private var trackTitle: String = "Unknown Title"
    private var trackAuthor: String = "Unknown Artist"
    private var isDraggingTimeline: Boolean = false

    private val playIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_play)!!
    private val pauseIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_pause)!!
    private val nextIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_next)!!
    private val prevIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_prev)!!
    private val defaultAlbumArtIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_audio)!!

    private val clipPath = Path()
    private var cornerRadiusPx = 0f
    private var paddingPx = 0f

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private val timelineBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 15f
        strokeCap = Paint.Cap.ROUND
    }

    private val timelineProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 15f
        strokeCap = Paint.Cap.ROUND
    }

    private val scrubberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val albumArtBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#E0E0E0".toColorInt()
        style = Paint.Style.FILL
    }

    private val albumArtRect = RectF()
    private val playPauseButtonRect = RectF()
    private val nextButtonRect = RectF()
    private val prevButtonRect = RectF()
    private val timelineRect = RectF()
    private val timelineTouchableRect = RectF()
    private val titleRect = RectF()
    private val authorRect = RectF()

    interface AudioPlayerListener {
        fun onPlayPauseClicked()
        fun onNextClicked()
        fun onPrevClicked()
        fun onTimelineChanged(newProgress: Float)
    }

    private var listener: AudioPlayerListener? = null

    fun setOnAudioPlayerListener(listener: AudioPlayerListener) {
        this.listener = listener
    }

    fun setAlbumArt(bitmap: Bitmap?) {
        albumArt = bitmap
        invalidate()
    }

    fun setIsPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }

    fun setProgress(progress: Float) {
        currentProgressRatio = progress.coerceIn(0f, 1f)
        invalidate()
    }

    fun setTrackInfo(title: String, author: String) {
        trackTitle = title
        trackAuthor = author
        invalidate()
    }

    init {
        isClickable = true
        val density = context.resources.displayMetrics.density
        cornerRadiusPx = CORNER_RADIUS_DP * density
        paddingPx = PADDING_DP * density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 920
        val desiredHeight = 1080
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var currentTop = 0f

        val albumArtSize = w - (paddingPx * 2)
        val albumArtLeft = paddingPx
        val albumArtTop = paddingPx

        albumArtRect.set(
            albumArtLeft,
            albumArtTop,
            albumArtLeft + albumArtSize,
            albumArtTop + albumArtSize
        )

        currentTop = albumArtRect.bottom

        val titleHeight = h * TITLE_HEIGHT_RATIO
        titleRect.set(w * 0.1f, currentTop, w * 0.9f, currentTop + titleHeight)
        currentTop = titleRect.bottom

        val authorHeight = h * AUTHOR_HEIGHT_RATIO
        authorRect.set(w * 0.1f, currentTop, w * 0.9f, currentTop + authorHeight)
        currentTop = authorRect.bottom

        currentTop += h * TIMELINE_TOP_MARGIN_RATIO
        val timelineHeight = h * TIMELINE_HEIGHT_RATIO
        timelineRect.set(w * 0.1f, currentTop, w * 0.9f, currentTop + timelineHeight)
        currentTop = timelineRect.bottom

        val touchableHeight = timelineRect.height() * 2
        timelineTouchableRect.set(
            timelineRect.left,
            timelineRect.centerY() - touchableHeight / 2,
            timelineRect.right,
            timelineRect.centerY() + touchableHeight / 2
        )

        val controlsHeight = h * CONTROLS_HEIGHT_RATIO
        val controlsTop = currentTop
        val buttonSize = controlsHeight * 0.85f
        val buttonSpacing = w * 0.08f
        val buttonCenterY = controlsTop + (controlsHeight / 2f)
        val buttonActualTop = buttonCenterY - (buttonSize / 2f)

        val playPauseButtonLeft = (w / 2f) - (buttonSize / 2f)
        playPauseButtonRect.set(
            playPauseButtonLeft,
            buttonActualTop,
            playPauseButtonLeft + buttonSize,
            buttonActualTop + buttonSize
        )

        val prevButtonLeft = playPauseButtonRect.left - buttonSpacing - buttonSize
        prevButtonRect.set(
            prevButtonLeft,
            buttonActualTop,
            prevButtonLeft + buttonSize,
            buttonActualTop + buttonSize
        )

        val nextButtonLeft = playPauseButtonRect.right + buttonSpacing
        nextButtonRect.set(
            nextButtonLeft,
            buttonActualTop,
            nextButtonLeft + buttonSize,
            buttonActualTop + buttonSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        clipPath.reset()
        clipPath.addRoundRect(albumArtRect, cornerRadiusPx, cornerRadiusPx, Path.Direction.CW)

        canvas.save()

        canvas.clipPath(clipPath)

        canvas.drawRect(albumArtRect, albumArtBackgroundPaint)
        if (albumArt != null) {
            canvas.drawBitmap(albumArt!!, null, albumArtRect, null)
        } else {
            val iconSize = min(albumArtRect.width(), albumArtRect.height()) * 0.5f
            val iconLeft = albumArtRect.centerX() - iconSize / 2
            val iconTop = albumArtRect.centerY() - iconSize / 2

            defaultAlbumArtIcon.setBounds(
                iconLeft.toInt(),
                iconTop.toInt(),
                (iconLeft + iconSize).toInt(),
                (iconTop + iconSize).toInt()
            )
            defaultAlbumArtIcon.draw(canvas)
        }

        canvas.restore()

        textPaint.textSize = titleRect.height() * 0.8f
        val titleY = titleRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(trackTitle, titleRect.centerX(), titleY, textPaint)

        textPaint.textSize = authorRect.height() * 0.8f
        val authorY = authorRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(trackAuthor, authorRect.centerX(), authorY, textPaint)

        val timelineY = timelineRect.centerY()
        val progressEndX = timelineRect.left + (timelineRect.width() * currentProgressRatio)
        canvas.drawLine(timelineRect.left, timelineY, timelineRect.right, timelineY, timelineBackgroundPaint)
        canvas.drawLine(timelineRect.left, timelineY, progressEndX, timelineY, timelineProgressPaint)
        val scrubberRadius = timelineProgressPaint.strokeWidth
        canvas.drawCircle(progressEndX, timelineY, scrubberRadius, scrubberPaint)

        val currentPlayPauseIcon = if (isPlaying) pauseIcon else playIcon
        currentPlayPauseIcon.bounds.set(playPauseButtonRect.toIntRect())
        currentPlayPauseIcon.draw(canvas)

        prevIcon.bounds.set(prevButtonRect.toIntRect())
        prevIcon.draw(canvas)

        nextIcon.bounds.set(nextButtonRect.toIntRect())
        nextIcon.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (timelineTouchableRect.contains(touchX, touchY)) {
                    isDraggingTimeline = true
                    updateTimelineProgress(touchX)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDraggingTimeline) {
                    updateTimelineProgress(touchX)
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isDraggingTimeline) {
                    isDraggingTimeline = false
                    return true
                }
                when {
                    playPauseButtonRect.contains(touchX, touchY) -> listener?.onPlayPauseClicked()
                    nextButtonRect.contains(touchX, touchY) -> listener?.onNextClicked()
                    prevButtonRect.contains(touchX, touchY) -> listener?.onPrevClicked()
                    timelineTouchableRect.contains(touchX, touchY) -> updateTimelineProgress(touchX)
                }
            }
        }
        return true
    }

    private fun updateTimelineProgress(touchX: Float) {
        val newProgress = (touchX - timelineRect.left) / timelineRect.width()
        listener?.onTimelineChanged(newProgress.coerceIn(0f, 1f))
    }

    private fun RectF.toIntRect(): Rect {
        return Rect(this.left.toInt(), this.top.toInt(), this.right.toInt(), this.bottom.toInt())
    }
}