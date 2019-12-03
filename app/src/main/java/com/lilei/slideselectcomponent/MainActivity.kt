package com.lilei.slideselectcomponent

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lilei.slideselectcomponent.vo.Item
import com.lilei.slideselectcomponent.widget.SlideSelectedRecyclerView
import com.shuwen.cloudVideoMeeting.TouchPositionTyper

class MainActivity : AppCompatActivity(), SlideSelectedRecyclerView.OnSlideSelectedChangedListener {
    /**数据*/
    private var mData: MutableList<Item>? = null
    /**选中的数据*/
    private var mSelected: MutableList<Item>? = null
    /**recyclerview控件*/
    private var mRecyclerView: SlideSelectedRecyclerView? = null
    /**适配器*/
    private var adapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView()
        initData()
        settingRecyclerView()
    }

    private fun findView() {
        mRecyclerView = findViewById(R.id.recyclerView)
    }

    /**
     * 初始化
     */
    private fun initData() {
        mData = ArrayList()
        for (index in 1..50) {
            val item = Item()
            mData!!.add(item)
        }
        mSelected = ArrayList()
    }

    /**
     * 配置recyclerview
     */
    private fun settingRecyclerView() {
        adapter = ItemAdapter(this, mData)
//        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = GridLayoutManager(this, 3)
        mRecyclerView!!.adapter = adapter
        mRecyclerView!!.setOnSlideSelectedChangedListener(this)
    }

    override fun onSlideSelectedChanged(position: Int, touchPositionTyper: TouchPositionTyper, columnCount: Int) {
        if (position > -1 && position < mData!!.size) {
            val remainder = position % columnCount
            var i = 0
            when (touchPositionTyper) {
                // 顶部
                TouchPositionTyper.TOUCH_POSITION_TOP, TouchPositionTyper.TOUCH_POSITION_BOTTOM -> {
                    for (index in 0..remainder) {
                        i = position - index
                        updateSelected(i)
                    }
                    val end = columnCount - remainder - 1
                    for (index in 0..end) {
                        i = position + index
                        updateSelected(i)
                    }
                    adapter!!.notifyItemRangeChanged(position - remainder - 1, columnCount)
                }
                // 中间
                TouchPositionTyper.TOUCH_POSITION_MIDDLE -> {
                    updateSelected(position)
                    adapter!!.notifyItemChanged(position)
                }
            }
        }
    }

    /**
     * 更新选中视图
     * @param position 位置索引
     */
    private fun updateSelected(position: Int) {
        val item = mData!!.get(position)
        val isSelected = mSelected!!.contains(item)
        if (isSelected) {
            mSelected!!.remove(item)
        } else {
            mSelected!!.add(item)
        }
        mData!!.get(position).isSelected = !isSelected
    }

    /**
     * 内容和样式适配器
     */
    private class ItemAdapter constructor(context: Context, data: MutableList<Item>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var mData: MutableList<Item>? = null
        private var mContext: Context? = null

        init {
            mData = data
            mContext = context
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item,  null))
        }

        override fun getItemCount(): Int {
            return if (mData == null) 0 else mData!!.size
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = mData!!.get(position)
            val viewHolder: ViewHolder = holder as ViewHolder
            if (item != null) {
                viewHolder.pic?.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            }
        }
    }

    /**
     * 视图句柄
     */
    private class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var pic: ImageView? = null
        init {
            pic = view?.findViewById(R.id.selected_iv)
        }
    }
}
