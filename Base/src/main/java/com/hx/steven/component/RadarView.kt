package com.hx.steven.component

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.min

class RadarView : View {
    companion object {
        // 默认宽高
        private const val DEFAULT_SIZE = 200

        // 默认主题颜色
        private val DEFAULT_COLOR = Color.parseColor("#91D7F4")

        // 默认圆圈数量
        private const val DEFAULT_CIRCLE_NUM = 3

        // 默认扫描的速度,几秒钟转一圈
        private const val DEFAULT_SWEEP_SPEED = 3.0f
    }

    // 圆圈和交叉线颜色
    private var mCircleColor = DEFAULT_COLOR

    // 圆圈数量
    private var mCircleNum = DEFAULT_CIRCLE_NUM

    // 扫描颜色
    private var mSweepColor = DEFAULT_COLOR

    // 是否显示交叉线
    private var isShowCross = true

    // 扫描时旋转角度
    private var mDegrees = 0f

    // 是否扫描中
    private var isScanning = true

    // 扫描的速度
    private var mSweepSpeed = DEFAULT_SWEEP_SPEED

    // 圆圈画笔
    private var mCirclePaint: Paint? = null

    // 扫描效果画笔
    private var mSweepPaint: Paint? = null


    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    fun init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        // 初始化圆圈画笔
        mCirclePaint = Paint()
        mCirclePaint?.color = mCircleColor
        mCirclePaint?.strokeWidth = 1f
        mCirclePaint?.style = Paint.Style.STROKE
        mCirclePaint?.isAntiAlias = true

        // 初始化扫描画笔
        mSweepPaint = Paint()
        mSweepPaint?.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        var w = widthSpecSize
        var h = heightSpecSize
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = DEFAULT_SIZE
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = heightSpecSize
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize
            h = DEFAULT_SIZE
        }
        setMeasuredDimension(w, h)
    }

    private var mCircleRadius = 0
    private var cx = 0
    private var cy = 0
    override fun onDraw(canvas: Canvas?) {
        // 计算圆的半径
        mCircleRadius =
            min(width - paddingLeft - paddingRight, height - paddingTop - paddingBottom) / 2
        // 计算圆心
        cx = (paddingLeft + (width - paddingLeft - paddingRight)) / 2
        cy = (paddingTop + (height - paddingTop - paddingBottom)) / 2
        // 绘制圆圈
        drawCircle(canvas, cx, cy, mCircleRadius)
        // 绘制交叉线
        if (isShowCross) {
            drawCross(canvas, cx, cy, mCircleRadius)
        }
        // 绘制扫描
        if (isScanning) {
            mDegrees = (mDegrees + (360 / mSweepSpeed / 60)) % 360;
            drawSweep(canvas, cx, cy, mCircleRadius)
        }
        invalidate()
    }


    /**
     * 绘制圆圈
     */
    private fun drawCircle(canvas: Canvas?, cx: Int, cy: Int, radius: Int) {
        for (i in 0 until mCircleNum) {
            mCirclePaint?.let {
                canvas?.drawCircle(
                    cx.toFloat(), cy.toFloat(), (radius - (radius / mCircleNum * i)).toFloat(),
                    it
                )
            }
        }
    }

    /**
     * 绘制交叉线
     */
    private fun drawCross(canvas: Canvas?, cx: Int, cy: Int, radius: Int) {
        mCirclePaint?.let {
            // 绘制水平线
            canvas?.drawLine(
                cx.toFloat(), (cy - radius).toFloat(), cx.toFloat(), (cy + radius).toFloat(),
                it
            )
            // 绘制垂直线
            canvas?.drawLine(
                (cx - radius).toFloat(), cy.toFloat(), (cx + radius).toFloat(), cy.toFloat(),
                it
            )
        }
    }

    /**
     * 绘制扫描
     */
    private fun drawSweep(canvas: Canvas?, cx: Int, cy: Int, radius: Int) {
        // 扇形透明渐变效果
        val sweepGradient = SweepGradient(
            cx.toFloat(), cy.toFloat(),
            intArrayOf(
                Color.TRANSPARENT,
                changeAlpha(mSweepColor, 0),
                changeAlpha(mSweepColor, 168),
                changeAlpha(mSweepColor, 255),
                changeAlpha(mSweepColor, 255)
            ),
            floatArrayOf(0.0f, 0.6f, 0.99f, 0.998f, 1f)
        )
        mSweepPaint?.shader = sweepGradient
        canvas?.rotate(-90 + mDegrees, cx.toFloat(), cy.toFloat())
        mSweepPaint?.let { canvas?.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), it) }
    }

    /**
     * dp转换px
     */
    private fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            resources.displayMetrics
        ).toInt()
    }

    /**
     * 改变颜色的透明度
     *
     * @param color
     * @param alpha
     * @return
     */
    private fun changeAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }
}