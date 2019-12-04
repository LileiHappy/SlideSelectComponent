package com.lilei.slideSelectComponent.type

/**
 * 滚动方向类型枚举
 * @author libai
 * @email 1542978431@qq.com（有问题或者交流可以发邮件到我的邮箱）
 * @since 2019-12-2
 * @version 1.0
 */
enum class ScrollDirectionTyper constructor(scrollDirectionType: Int) {
    /**往上滚动*/
    SCROLL_DIRECTION_UP(-2),
    /**往下滚动*/
    SCROLL_DIRECTION_BOTTOM(-3),
    /**未知*/
    SCROLL_DIRECTION_UNKNOWN(0);

    /**当前滚动枚举类型对应的类型值*/
    private var mScrollDirectionType = 0

    init {
        mScrollDirectionType = scrollDirectionType
    }

    /**
     * 获取滚动方向
     * @return 滚动方向值
     */
    public fun getScrollDirection(): Int {
        return mScrollDirectionType
    }

    /**
     * 获取滚动方向类型枚举
     * @param scrollDirectionType 滚动方向类型值
     * @return 滚动方向类型
     */
    public fun getScrollDirectionTyper(scrollDirectionType: Int): ScrollDirectionTyper {
        var scrollDirectionTyper: ScrollDirectionTyper
        when (scrollDirectionType) {
            -2 -> scrollDirectionTyper = SCROLL_DIRECTION_UP
            -3 -> scrollDirectionTyper = SCROLL_DIRECTION_BOTTOM
            else -> scrollDirectionTyper = SCROLL_DIRECTION_UNKNOWN
        }
        return scrollDirectionTyper
    }

    /**
     * 获取滚动方向
     * @param scrollDirectionTyper 滚动方向枚举类型
     * @return 滚动方向类型值
     */
    public fun getScrollDirection(scrollDirectionTyper: ScrollDirectionTyper): Int {
        var scrollDirectionType: Int
        when (scrollDirectionTyper) {
            SCROLL_DIRECTION_UP -> scrollDirectionType = -2
            SCROLL_DIRECTION_BOTTOM -> scrollDirectionType = -3
            else -> scrollDirectionType = 0
        }
        return scrollDirectionType
    }
}