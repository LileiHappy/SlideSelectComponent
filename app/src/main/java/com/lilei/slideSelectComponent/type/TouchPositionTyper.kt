package com.lilei.slideSelectComponent.type

/**
 * 触摸点位置类型枚举
 * @author libai
 * @email 1542978431@qq.com（有问题或者交流可以发邮件到我的邮箱）
 * @since 2019-12-2
 * @version 1.0
 */
enum class TouchPositionTyper constructor(touchPositionType: Int) {
    /**触摸点位于顶部：在列表中展示视图的第一排*/
    TOUCH_POSITION_TOP(0),
    /**触摸点位于底部：在列表中展示视图的最后一排*/
    TOUCH_POSITION_BOTTOM(1),
    /**触摸点位于中间*/
    TOUCH_POSITION_MIDDLE(2),
    /**触摸点位置未知*/
    TOUCH_POSITION_UNKNOWN(3);

    /**当前枚举对应的触摸点值*/
    private var mTouchPositionType = 3

    init {
        mTouchPositionType = touchPositionType
    }

    /**
     * 获取触摸点类型值
     * @return 类型值
     */
    public fun getTouchPositionType(): Int {
        return mTouchPositionType
    }

    /**
     * 获取触摸点类型值对应的触摸点类型
     * @param touchPositionType 类型值
     * @return 对应的类型枚举
     */
    public fun getTouchPositionTyper(touchPositionType: Int): TouchPositionTyper {
        var touchPositionTyper: TouchPositionTyper
        when (touchPositionType) {
            // 顶部
            0 -> touchPositionTyper = TOUCH_POSITION_TOP
            // 底部
            1 -> touchPositionTyper = TOUCH_POSITION_BOTTOM
            // 中间
            2 -> touchPositionTyper = TOUCH_POSITION_MIDDLE
            // 其他都认为是未知
            else -> touchPositionTyper = TOUCH_POSITION_UNKNOWN
        }
        return touchPositionTyper
    }

    /**
     * 获取触摸点类型对应的类型值
     * @param touchPositionTyper 类型
     * @return 对应的类型
     */
    public fun getTouchPositionType(touchPositionTyper: TouchPositionTyper): Int {
        var touchPositionType: Int
        when (touchPositionTyper) {
            // 顶部
            TOUCH_POSITION_TOP -> touchPositionType = 0
            // 底部
            TOUCH_POSITION_BOTTOM -> touchPositionType = 1
            // 中间
            TOUCH_POSITION_MIDDLE -> touchPositionType = 2
            // 其他的都认为未知
            else -> touchPositionType = 3
        }
        return touchPositionType
    }
}