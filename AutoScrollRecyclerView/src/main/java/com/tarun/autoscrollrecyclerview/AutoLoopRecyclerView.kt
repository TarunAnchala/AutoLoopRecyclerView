package com.tarun.autoscrollrecyclerview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoLoopRecyclerView : RecyclerView {
    val loopHandler = Handler()

    // flag to check if user is scrolling or not
    var isDragged = false

    // flag to check if auto scroll is on
    // This flag is required because we should not start scroll twice i.e  when onVisibiltyChanged and onAttachedtoWindow gets called
    var isAutoScrollOn = false

    // runnable to auto scroll banners for every 5sec
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (layoutManager is LinearLayoutManager) {
                val linearLayoutManager = layoutManager as LinearLayoutManager
                val pos = linearLayoutManager.findFirstVisibleItemPosition()
                if (adapter != null) {
                    if (pos >= adapter!!.itemCount - 1) {
                        // if last banner, then scroll to first banner
                        scrollToPosition(0)
                    } else {
                        // scrolling to next banner
                        smoothScrollToPosition(pos + 1)
                    }
                    loopHandler.postDelayed(this, ITEM_AUTO_SCROLL_TIMER)
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoScroll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoScroll()
    }

    /**
     * API to stop auto scroll
     */
    fun stopAutoScroll() {

        if (isAutoScrollOn) {
            isAutoScrollOn = false
            loopHandler.removeCallbacks(runnable)
        }
    }

    /**
     * API to start auto scroll
     */
    fun startAutoScroll() {
        // start auto scroll only if it is off
        if (!isAutoScrollOn) {
            isAutoScrollOn = true
            loopHandler.postDelayed(runnable, ITEM_AUTO_SCROLL_TIMER)
        }
    }

    companion object {
        private const val TAG = "AutoScrollRecyclerView"
        //Timer delay between items scroll in recycler view
        private const val ITEM_AUTO_SCROLL_TIMER = 5000L
    }

    override fun onScrollStateChanged(newState: Int) {
        if (newState == SCROLL_STATE_IDLE) {
            if (isDragged) {
                //After user scroll start auto loop handler
                isDragged = false
                startAutoScroll()
            } else if (newState == SCROLL_STATE_DRAGGING) {
                //When user tries to scroll
                stopAutoScroll()
                isDragged = true

            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            // If rv is visible,start auto scroll
            startAutoScroll()
        } else {
            // If rv is invisible,stop auto scroll
            stopAutoScroll()
        }
    }

}