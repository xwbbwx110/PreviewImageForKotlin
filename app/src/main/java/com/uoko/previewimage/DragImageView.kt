package com.uoko.previewimage

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.chrisbanes.photoview.PhotoView

/**
 * 作者: xwb on 2018/11/19
 * 描述:根据滑动，重新绘制imgaeview，实现拖拽返回效果
 * 基于
 *  https://github.com/chrisbanes/PhotoView
 */
class DragImageView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : PhotoView(context, attr, defStyle) {
    private val mPaint: Paint

    // downX
    private var mDownX: Float = 0f
    // down Y
    private var mDownY: Float = 0f

    private var mTranslateY: Float = 0f
    private var mTranslateX: Float = 0f
    private var mWidth: Int = 0
    private var mHeight: Int = 0
     var mScale = 1f //当前的缩放进度值
     var minScale = 0.0f //最小缩放到0.3倍
    private val mScaleLevel = 0.5f //缩放的级别0.5倍
    private val mMaxScaleValue = 1.0f
    private var mAlpha = 255
    private var canFinish = false
    private var isAnimate = false

    val DURATION = 360L


    private val alphaAnimation: ValueAnimator
        get() {
            val animator = ValueAnimator.ofInt(mAlpha, 255)
            animator.duration = DURATION
            animator.addUpdateListener { valueAnimator -> mAlpha = valueAnimator.animatedValue as Int }

            return animator
        }

    private val translateYAnimation: ValueAnimator
        get() {
            val animator = ValueAnimator.ofFloat(mTranslateY, 0.0f)
            animator.duration = DURATION
            animator.addUpdateListener { valueAnimator -> mTranslateY = valueAnimator.animatedValue as Float }

            return animator
        }

    private val translateXAnimation: ValueAnimator
        get() {
            val animator = ValueAnimator.ofFloat(mTranslateX, 0.0f)
            animator.duration = DURATION
            animator.addUpdateListener { valueAnimator -> mTranslateX = valueAnimator.animatedValue as Float }

            return animator
        }

    private val scaleAnimation: ValueAnimator
        get() {
            val animator = ValueAnimator.ofFloat(mScale, 1.0f)
            animator.duration = DURATION
            animator.addUpdateListener { valueAnimator ->
                mScale = valueAnimator.animatedValue as Float
                invalidate()
            }

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    isAnimate = true
                }

                override fun onAnimationEnd(animator: Animator) {
                    isAnimate = false
                    animator.removeAllListeners()
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            return animator
        }

    init {
        mPaint = Paint()
        mPaint.color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.alpha = mAlpha
        canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mPaint)//对角线，形成一个矩形
        canvas.translate(mTranslateX, mTranslateY)
        canvas.scale(mScale, mScale, (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        alpha = mScale
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }


    /**
     * 按时间分发体系来说，dispatchTouchEvent不应该处理事件，只应该用来决定是否传递事件，最开始在纠结为什么不走onTouchEvent
     * 原来PhotoView设置了OnTouchListener监听，所以view类里面的dispatchTouchEvent内部 直接走OnTouchListener了，
     * 所以这里只能用dispatchTouchEvent来代替事件处理了
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //在没有缩放的情况下，才可以拖动
        if (scale == 1f) {

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    mDownX = event.x
                    mDownY = event.y

                    canFinish = !canFinish
                }
                MotionEvent.ACTION_MOVE -> {

                    if( Math.abs(event.x - mDownX) > Math.abs(event.y - mDownY)){//如果当前是横向滑动，就把时间传递给下去
                        return super.dispatchTouchEvent(event)
                    }

                    if (event.pointerCount == 1) {//只有一根手指触摸的时候才可以拖动
                        mTranslateX =  event.x - mDownX
                        mTranslateY = event.y - mDownY

                        if (mTranslateY < 0f) {
                            mTranslateY = 0f
                        }

                        val percent = mTranslateY / 600
                        if (mScale>=minScale &&  minScale<=mMaxScaleValue) {
                            val percentVa = percent*mScaleLevel
                            mScale = mMaxScaleValue - percentVa //当前缩放值

                            mAlpha = (255 * (mMaxScaleValue - percentVa)).toInt() //颜色透明值
                            if (mAlpha > 255) {
                                mAlpha = 255
                            } else if (mAlpha < 0) {
                                mAlpha = 0
                            }
                        }

                        if (mScale < minScale) {
                            mScale = minScale
                        } else if (mScale > mMaxScaleValue) {
                            mScale = mMaxScaleValue
                        }
                        invalidate()
                        return true //不传递事件了

                    }
                    //防止下拉的时候双手缩放
                    if (mTranslateY >= 0 && mScale < 0.95) {
                        return true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (event.pointerCount == 1) {

                        if (mTranslateY > 600) {
                                val translateXAnimator = ValueAnimator.ofFloat(mScale, 0.0f)
                                translateXAnimator.addUpdateListener { valueAnimator ->

                                    mScale = valueAnimator.animatedValue as Float

                                    if(mScale<=0.0f){
//                                        mExitListener?.onExit(this, mTranslateX, mTranslateY, mWidth.toFloat(), mHeight.toFloat())

                                        mExitListener(this, mTranslateX, mTranslateY, mWidth.toFloat(), mHeight.toFloat())

                                    }else{
                                        invalidate()
                                    }

                                }
                                translateXAnimator.duration = 300
                                translateXAnimator.start()
                        } else {
                            performAnimation()
                        }
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }


    /**
     * 已经被OnTouchListener优先处理了
     */
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//
//            MotionEvent.ACTION_DOWN -> Log.e("onTouchEvent", "ac_down")
//
//            MotionEvent.ACTION_MOVE -> Log.e("onTouchEvent", "ac_move")
//
//
//            MotionEvent.ACTION_UP -> Log.e("onTouchEvent", "ac_up")
//        }
//        return super.onTouchEvent(event)
//    }


    private fun performAnimation() {
        scaleAnimation.start()
        translateXAnimation.start()
        translateYAnimation.start()
        alphaAnimation.start()
    }



    private lateinit var mExitListener : (DragImageView,Float,Float,Float,Float) ->Unit



    fun setOnExitListener(listener: (view: DragImageView, translateX: Float, translateY: Float, w: Float, h: Float) -> Unit) {
        mExitListener = listener
    }

    interface OnExitListener {
        fun onExit(view: DragImageView, translateX: Float, translateY: Float, w: Float, h: Float)
    }


}
