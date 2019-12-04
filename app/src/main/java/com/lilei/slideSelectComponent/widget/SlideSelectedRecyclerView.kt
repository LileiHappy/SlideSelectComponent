package com.lilei.slideSelectComponent.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lilei.slideSelectComponent.type.ScrollDirectionTyper
import com.lilei.slideSelectComponent.type.TouchPositionTyper

/**
 * 滑动选择列表视图控件
 * @author lilei
 * @email 1542978431@qq.com（有问题或者交流可以发邮件到我的邮箱）
 * @since 2019-12-2
 * @version 1.0
 */
class SlideSelectedRecyclerView constructor(context: Context, attrs: AttributeSet?, defStyle: Int): RecyclerView(context, attrs, defStyle) {
    /**按下位置位置*/
    private val NO_POINT = 0f
    /**位置坐标未知*/
    private val NO_LOCATION = Integer.MIN_VALUE
    /**尺寸未知*/
    private val NO_SIZE = 0
    /**没有child view*/
    private val EMPTY = 0
    /**默认触摸child view位置索引*/
    private val DEFAULT_POSITION = -1
    /**无需自动滚动到下一行*/
    private val DO_NOT_AUTO_SCRLL_NEXT_ROW = false
    /**默认自动滚动距离*/
    private val DEFAULT_AUTO_SCROLL_DISTANCE = 66
    /**默认滑动距离阈值*/
    private val SLIDE_DISTANCE_THRESHOLD = 80

    /**上下文*/
    private var mContext: Context? = null

    /**按下x坐标*/
    private var mDownX = NO_POINT
    /**按下y坐标*/
    private var mDownY = NO_POINT
    /**当前触摸点x坐标*/
    private var mCurrentX = NO_POINT
    /**当前触摸点x坐标*/
    private var mCurrentY = NO_POINT
    /**recyclerView在屏幕中的左上角x坐标*/
    private var mLocationX = NO_LOCATION
    /**recyclerView在屏幕中的左上角y坐标*/
    private var mLocationY = NO_LOCATION

    /**recyclerView控件宽*/
    private var mWidth = NO_SIZE
    /**recyclerView控件高*/
    private var mHeight = NO_SIZE
    /**child view高度的半值*/
    private var mChildViewHalfHeight = NO_SIZE
    /**child view宽度的半值*/
    private var mChildViewHalfWidth = NO_SIZE
    /**child view高度的四分之一*/
    private var mChildViewQuarterHeight = NO_SIZE

    /**认为滑动的水平阈值*/
    private var mSlideDistanceThresholdHorizontal = SLIDE_DISTANCE_THRESHOLD
    /**认为滑动的垂直阈值*/
    private var mSlideDistanceThresholdVertical = SLIDE_DISTANCE_THRESHOLD

    /**列数：一行有多少个child view*/
    private var mColumnCount = EMPTY
    private var mDownPosition = DEFAULT_POSITION
    /**当前触摸的child view的位置索引*/
    private var mCurrentPosition = DEFAULT_POSITION

    /**自动滚动到下行标志*/
    private var isAutoScrollNextRow = DO_NOT_AUTO_SCRLL_NEXT_ROW
    /**自动滚动的距离*/
    private var mAutoScrollDistance = DEFAULT_AUTO_SCROLL_DISTANCE

    /**滑动选择监听*/
    private var mSlideSelectedChangedListener: OnSlideSelectedChangedListener? = null

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    init {
        mContext = context
    }

    /**
     * 设置自动滚动下行
     * @param isAutoScrollNextRow 自动滚动到下行标志
     */
    public fun setAutoScrollNextRow(isAutoScrollNextRow: Boolean) {
        this.isAutoScrollNextRow = isAutoScrollNextRow
    }

    /**
     * 设置自动滚动的距离
     * @param autoScrollDistance 自动滚动的距离
     */
    public fun setAutoScrollDistance(autoScrollDistance: Int) {
        mAutoScrollDistance = autoScrollDistance
    }

    /**
     * 设置滑动变化监听
     * @param listener 监听对象
     */
    public fun setOnSlideSelectedChangedListener(listener: OnSlideSelectedChangedListener) {
        mSlideSelectedChangedListener = listener
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            // 获取recyclerview的尺寸
            getRecyclerViewSize()
            // 获取recyclerview的坐标
            getLocation()
            // 获取child view高度的半值
            getChildViewHalfHeight()
            // 获取列数
            getColumnCount()
            // 获取适配child view高度后的自动滚动值，如果使用方设置了则使用设置的值
            getMatchChildViewHeightScrollDistance()
            // 获取适配child view的滑动阈值
            getMatchChildViewSlideDistanceThreshold()
        }
    }

    /**
     * 获取recyclerview的尺寸
     */
    private fun getRecyclerViewSize() {
        if (NO_SIZE == mWidth) {
            mWidth = measuredWidth
            mHeight = measuredHeight
        }
    }

    /**
     * 获取recyclerview在屏幕中的位置信息（左上角坐标）
     */
    private fun getLocation() {
        if (NO_LOCATION == mLocationX) {
            var location = IntArray(2)
            // 获取recyclerview在屏幕中的位置信息
            getLocationOnScreen(location)
            mLocationX = location[0]
            mLocationY = location[1]
        }
    }

    /**
     * 获取child view高度的半值
     */
    private fun getChildViewHalfHeight() {
        if (NO_SIZE == mChildViewHalfHeight && childCount > EMPTY) {
            // 获取第一个child view
            val firstChildView = getChildAt(0)
            if (firstChildView != null) {
                // 获取高度的半值
                mChildViewHalfWidth = firstChildView.measuredWidth.shr(1)
                mChildViewHalfHeight = firstChildView.measuredHeight.shr(1)
                mChildViewQuarterHeight = mChildViewHalfHeight.shr(1)
            }
        }
    }

    /**
     * 获取列数
     */
    private fun getColumnCount() {
        if (layoutManager != null) {
            when (layoutManager) {
                is GridLayoutManager -> {
                    // 获取设置的列数
                    mColumnCount = (layoutManager as GridLayoutManager).spanCount
                }
                is LinearLayoutManager -> {
                    // linearLayoutManager则一行只有一个
                    mColumnCount = 1
                }
                is StaggeredGridLayoutManager -> {
                    mColumnCount = (layoutManager as StaggeredGridLayoutManager).spanCount
                }
                // 目前只支持recyclerView自带的LayoutManager
                else -> {
                    throw RuntimeException("is not support LayoutManager!")
                }
            }
        }
    }

    /**
     * 获取适配child view高度后的滚动距离，滚动距离为child view高度的四分之一
     */
    private fun getMatchChildViewHeightScrollDistance() {
        // 说明使用者没有自定义自动滚动的距离
        if (DEFAULT_AUTO_SCROLL_DISTANCE == mAutoScrollDistance) {
            // 适配child view高度的自动滚动距离为child view高度的四分之一
            mAutoScrollDistance = mChildViewHalfHeight.shr(1)
        }
    }

    private fun getMatchChildViewSlideDistanceThreshold() {
        if (SLIDE_DISTANCE_THRESHOLD == mSlideDistanceThresholdHorizontal) {
            mSlideDistanceThresholdHorizontal = mChildViewHalfWidth
            mSlideDistanceThresholdVertical = mChildViewHalfHeight
        }
    }

    /**
     * 在分发触摸事件中处理滑动经过的child view的位置索引
     * @param ev 运动事件
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            // 按下或移动
            MotionEvent.ACTION_DOWN -> {
                // 记录当前触摸点坐标
                mDownX = ev.x
                mDownY = ev.y
                mDownPosition = getTouchChildViewPosition(mDownX, mDownY)
                mSlideSelectedChangedListener?.onDownPositionChanged(mDownPosition)
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentX = ev.x
                mCurrentY = ev.y
                if (Math.abs(mCurrentX - mDownX) > mSlideDistanceThresholdHorizontal
                    || Math.abs(mCurrentY -mDownY) > mSlideDistanceThresholdVertical) {
                    // 当前触摸点点中的是列表中展示的最后一排child view的下半部分
                    if (mHeight + mLocationY - mChildViewHalfHeight - mChildViewQuarterHeight < mCurrentY) { // 底部
                        doScrollAndCallback(TouchPositionTyper.TOUCH_POSITION_BOTTOM)
                    } else if (mCurrentY < mChildViewQuarterHeight + mLocationY) { // 顶部，点中的是列表中展示的第一排child view的上半部分
                        doScrollAndCallback(TouchPositionTyper.TOUCH_POSITION_TOP)
                    } else { // 中间
                        doScrollAndCallback(TouchPositionTyper.TOUCH_POSITION_MIDDLE)
                    }
                    return true
                } else {
                    return super.dispatchTouchEvent(ev)
                }
            }
            // 抬起
            MotionEvent.ACTION_UP -> {
                mDownX = NO_POINT
                mDownY = NO_POINT
                mCurrentX = NO_POINT
                mCurrentY = NO_POINT
                mDownPosition = DEFAULT_POSITION
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 滚动recyclerview并且通知使用方
     * @param touchPositionTyper 触摸点位置类型
     */
    private fun doScrollAndCallback(touchPositionTyper: TouchPositionTyper) {
        if (adapter != null && layoutManager != null && mSlideSelectedChangedListener != null) {
            val position = getTouchChildViewPosition(mCurrentX, mCurrentY)
            if (position != -1) {
                // 获取需要滚动到的位置
                val scrollPosition = getScrollPosition(position, touchPositionTyper)
                // 丝滑滚动
                smoothScroll(scrollPosition)
                // 如果位置不一致则通知使用方
                if (mCurrentPosition != position)
                    mSlideSelectedChangedListener?.onSlideSelectedChanged(position, mColumnCount)
                // 更新最新位置索引
                mCurrentPosition = position
            }
        }
    }

    /**
     * 获取触摸点的child view位置索引
     * @param positionX 触摸点x坐标
     * @param positionY 触摸点y坐标
     */
    private fun getTouchChildViewPosition(positionX: Float, positionY: Float): Int {
        // 获取当前坐标点对应的child view
        val childView: View? = findChildViewUnder(positionX, positionY)
        // 获取该视图对应的位置索引，如果没有对应的视图则返回-1，否则返回对应位置索引
        return if (childView == null) -1 else getChildAdapterPosition(childView)
    }

    /**
     * 获取需要滚动到的位置
     * @param position 触摸child view的位置索引
     * @param touchPositionTyper 触摸位置类型
     */
    private fun getScrollPosition(position: Int, touchPositionTyper: TouchPositionTyper): Int {
        // 需要滚动到的位置
        var scrollPosition = -1
        when (touchPositionTyper) {
            // 顶部
            TouchPositionTyper.TOUCH_POSITION_TOP -> {
                // 需要自动滚动
                if (isAutoScrollNextRow) {
                    // 校验是否范围越界
                    if (!isOutRange(position))
                        // 超过最小值则使用最小值，否则使用计算值
                        scrollPosition = if (position - mColumnCount < 0) 0 else position - mColumnCount
                } else {
                    // 网上滚动
                    scrollPosition = ScrollDirectionTyper.SCROLL_DIRECTION_UP.getScrollDirection()
                }
            }
            // 底部
            TouchPositionTyper.TOUCH_POSITION_BOTTOM -> {
                if (isAutoScrollNextRow) {
                    if (!isOutRange(position))
                    scrollPosition = if (position + mColumnCount >= adapter!!.itemCount) adapter!!.itemCount - 1
                    else position + mColumnCount
                } else {
                    scrollPosition = ScrollDirectionTyper.SCROLL_DIRECTION_BOTTOM.getScrollDirection()
                }
            }
            // 中间，位于中间则不需要做自动滚动处理
            TouchPositionTyper.TOUCH_POSITION_MIDDLE -> { }
        }
        return scrollPosition
    }

    /**
     * 丝滑滚动到指定位置
     * @param scrollPosition 待滚动到的位置
     */
    private fun smoothScroll(scrollPosition: Int) {
        when (scrollPosition) {
            // 网上滚动
            ScrollDirectionTyper.SCROLL_DIRECTION_UP.getScrollDirection() -> {
                smoothScrollBy(0, -mAutoScrollDistance)
            }
            // 往下滚动
            ScrollDirectionTyper.SCROLL_DIRECTION_BOTTOM.getScrollDirection() -> {
                smoothScrollBy(0, mAutoScrollDistance)
            }
            else -> {
                if (scrollPosition > -1) {
                    smoothScrollToPosition(scrollPosition)
                }
            }
        }
    }

    /**
     * 判断是否越界
     * @param position 位置索引
     */
    private fun isOutRange(position: Int): Boolean {
        return if (adapter == null) true else position < 0 || position >= adapter!!.itemCount
    }

    /**
     * 滑动选中改变监听
     */
    public interface OnSlideSelectedChangedListener {
        /**
         * 滑动选中改变监听回调
         * @param currentPosition 位置索引
         * @param columnCount 列数
         */
        public fun onSlideSelectedChanged(currentPosition: Int, columnCount: Int)

        /**
         * 按下点改变监听回调
         * @param downPosition 按下位置索引
         */
        public fun onDownPositionChanged(downPosition: Int)
    }
}