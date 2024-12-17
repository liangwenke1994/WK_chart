package com.wk.view.tab

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wk.chart.databinding.ItemMoreTabBinding
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.TimeType
import com.wk.view.ext.binding

/**
 * 更多子tab中的adapter
 */
class MoreTabAdapter(private val chartTabListener: ChartTabListener?) :
    RecyclerView.Adapter<MoreTabAdapter.MoreTabHolder>(), View.OnClickListener {

    private val mData: ArrayList<TabTimeBean> = ArrayList()
    private var mRecoveryPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreTabHolder {
        return MoreTabHolder(parent.binding<ItemMoreTabBinding>().value)
    }

    override fun onBindViewHolder(holder: MoreTabHolder, position: Int) {
        getItem(position)?.let {
            holder.mItemType?.let { rb ->
                rb.isSelected = it.isSelected
                rb.tag = position
                rb.text = it.tabName
                rb.setOnClickListener(this)
            }
        }
    }

    fun itemRecovery() {
        getItem(mRecoveryPosition)?.let { recovery ->
            recovery.isSelected = false
            notifyItemChanged(mRecoveryPosition)
            mRecoveryPosition = -1
        }
    }

    private fun itemSelected(checkedPosition: Int): TabTimeBean? {
        getItem(checkedPosition)?.let { checked ->
            checked.isSelected = true
            notifyItemChanged(checkedPosition)
            mRecoveryPosition = checkedPosition
            return checked
        }
        return null
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<TabTimeBean>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): TabTimeBean? {
        val size = mData.size
        if (position < 0 || position >= size) {
            return null
        }
        return mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getSelectedItem(): TabTimeBean? {
        return getItem(mRecoveryPosition)
    }

    fun selectedItem(type: TimeType, @IndexType indexType: Int): TabTimeBean? {
        for (i in mData.indices) {
            val item = mData[i]
            if (item.indexType == indexType && item.tabValue == type) {
                itemRecovery()
                itemSelected(i)?.let {
                    return it
                }
                return null
            }
        }
        return null
    }

    override fun onClick(v: View?) {
        v?.tag?.let {
            itemRecovery()
            itemSelected(it as Int)?.let { bean ->
                chartTabListener?.onTimeTypeChange(bean.tabValue, bean.indexType)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null
    }

    inner class MoreTabHolder(mBinding: ItemMoreTabBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var mItemType: TextView? = null

        init {
            mItemType = mBinding.itemType
        }
    }
}