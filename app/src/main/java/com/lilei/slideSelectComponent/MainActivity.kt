package com.lilei.slideSelectComponent

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lilei.slideSelectComponent.vo.Item
import com.lilei.slideSelectComponent.widget.SlideSelectedRecyclerView

/**
 * 展示页面
 * @author lilei
 * @email 1542978431@qq.com（有问题或者交流可以发邮件到我的邮箱）
 * @since 2019-12-2
 * @version 1.0
 */
class MainActivity : AppCompatActivity(), SlideSelectedRecyclerView.OnSlideSelectedChangedListener {
    /**数据*/
    private var mData: MutableList<Item>? = null
    /**选中的数据*/
    private var mSelected: MutableList<Item>? = null
    /**recyclerview控件*/
    private var mRecyclerView: SlideSelectedRecyclerView? = null
    /**适配器*/
    private var mAdapter: ItemAdapter? = null
    /**按下child view的选中状态标志*/
    private var isSelected = false
    /**按下child view的位置索引*/
    private var mDownPosition = -1

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
        for (index in 1..51) {
            val item = Item()
            mData!!.add(item)
        }
        mSelected = ArrayList()
    }

    /**
     * 配置recyclerview
     */
    private fun settingRecyclerView() {
        mAdapter = ItemAdapter(this, mData)
//        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = GridLayoutManager(this, 3)
        mRecyclerView!!.adapter = mAdapter
        mRecyclerView!!.setOnSlideSelectedChangedListener(this)
    }

    override fun onDownPositionChanged(downPosition: Int) {
        // 校验是否越界
        if (!isOutRange(downPosition)) {
            // 获取按下时对应的child view的选中状态
            isSelected = mData!!.get(downPosition).isSelected
            // 记录按下的child view对应的位置索引
            mDownPosition = downPosition
        }
    }

    override fun onSlideSelectedChanged(position: Int, columnCount: Int) {
        if (position > -1 && position < mData!!.size) {
            // 按下位置索引大于当前位置索引
            if (mDownPosition > position) {
                // 为了包含按下点，则结束索引为按下位置索引加1
                val endPosition = mDownPosition + 1
                // 依次更新每个
                for (index in position..endPosition) {
                    updateSelected(index)
                }
                // 刷新更新的视图
                mAdapter!!.notifyItemRangeChanged(position, mDownPosition - position + 1)
            } else { // 按下位置索引小于等于当前位置索引
                val endPosition = position + 1
                val startPosition = mDownPosition
                for (index in startPosition..endPosition) {
                    updateSelected(index)
                }
                mAdapter!!.notifyItemRangeChanged(startPosition, position - startPosition + 1)
            }
        }
    }

    /**
     * 更新选中视图
     * @param position 位置索引
     */
    private fun updateSelected(position: Int) {
        // 校验是否越界
        if (!isOutRange(position)) {
            // 获取对应的数据源
            val item = mData!!.get(position)
            // 原数据源未选中，则需要选中
            if (!isSelected) {
                mSelected!!.add(item)
            } else { // 原数据源选中，则从选中中移除
                mSelected!!.remove(item)
            }
            // 更新选中标志
            mData!!.get(position).isSelected = !isSelected
        }
    }

    /**
     * 越界校验
     * @param position 位置索引
     */
    private fun isOutRange(position: Int): Boolean {
        return position < 0 || position >= mData!!.size
    }

    /**
     * 内容和样式适配器
     */
    private class ItemAdapter constructor(context: Context, data: MutableList<Item>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        /**数据*/
        private var mData: MutableList<Item>? = null
        /**上下文*/
        private var mContext: Context? = null

        init {
            mData = data
            mContext = context
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item, null))
        }

        override fun getItemCount(): Int {
            return if (mData == null) 0 else mData!!.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = mData!!.get(position)
            val viewHolder: ViewHolder = holder as ViewHolder
            if (item != null) {
                viewHolder.pic?.setImageResource(if (item.isSelected) R.mipmap.ic_launcher else R.mipmap.ic_launcher_round)
            }
            viewHolder?.image?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(mContext, "这里只是模仿需要处理点击动作", Toast.LENGTH_SHORT).show()
                }
            })
            // 选中或取消选中
            viewHolder.pic?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    var isSelect = item?.isSelected
                    if (viewHolder.pic != null)
                        showSelect(viewHolder.pic!!, isSelect)
                    item?.isSelected = !isSelect
                }
            })
        }

        private fun showSelect(image: ImageView, isSelected: Boolean) {
            image.setImageResource(if (isSelected) R.mipmap.ic_launcher else R.mipmap.ic_launcher_round)
        }
    }

    /**
     * 视图句柄
     */
    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var pic: ImageView? = null
        var image: ImageView? = null

        init {
            pic = view?.findViewById(R.id.selected_iv)
            image = view?.findViewById(R.id.image)
        }
    }
}
