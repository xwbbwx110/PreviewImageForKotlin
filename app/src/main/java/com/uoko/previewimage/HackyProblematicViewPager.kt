
package com.uoko.previewimage

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 作者: xwb on 2018/11/19
 * 防止多指触摸指针异常问题和处理 横竖滑动 事件冲突问题
 */
class HackyProblematicViewPager   @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) : ViewPager(context, attr) {
    private var mDownX = 0f
    private var mDownY = 0f
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {

            when (event.action) {
                MotionEvent.ACTION_DOWN ->{
                    if(mDownX>0f)
                    mDownX = 0f

                    if(mDownY>0f)
                        mDownY = 0f

                    mDownX = event.x
                    mDownY = event.y
                }

                MotionEvent.ACTION_MOVE -> {
                    return    Math.abs( event.x - mDownX) > Math.abs(event.y - mDownY) //横向滑动拦截事件，反之不拦截事件
                }

                MotionEvent.ACTION_UP ->
                    mDownX = 0f
            }
            return super.onInterceptTouchEvent(event)
        } catch (ex: IllegalArgumentException) {
            //防止多指触摸指针异常问题
            return false
        }

    }


}
