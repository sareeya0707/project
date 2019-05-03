package com.csc48.deliverycoffeeshop.adapter

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.utils.*

class OrdersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mData: List<OrderModel> = listOf()
    var userRole: Int = USER_ROLE_CUSTOMER

    private var callback: OnSelectListener? = null

    interface OnSelectListener {
        fun onSelectItem(id: String?, userRole: Int)
    }

    fun setOnSelectListener(callback: OnSelectListener) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindViews(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val rootItem = itemView.findViewById<RelativeLayout>(R.id.rootItem)
        private val tvOrderName = itemView.findViewById<TextView>(R.id.tvOrderName)
        private val tvOrderAddress = itemView.findViewById<TextView>(R.id.tvOrderAddress)
        private val tvCreateDate = itemView.findViewById<TextView>(R.id.tvCreateDate)
        private val layoutOrderStatus = itemView.findViewById<RelativeLayout>(R.id.layoutOrderStatus)
        private val tvOrderStatus = itemView.findViewById<TextView>(R.id.tvOrderStatus)

        init {
            rootItem.setOnClickListener {
                callback?.onSelectItem(mData[adapterPosition].key, userRole)
            }
        }

        fun bindViews(orderModel: OrderModel) {
            tvOrderName.text = orderModel.shipping_name ?: "-"
            tvOrderAddress.text = orderModel.shipping_address ?: "-"
            tvCreateDate.text = DateFormat.format("dd-MM-yyyy hh:mm:ss", orderModel.create_at).toString()

            when (orderModel.status) {
                ORDER_STATUS_WAITING -> {
                    tvOrderStatus.text = context.getText(R.string.orderWaiting)
                    layoutOrderStatus.setBackgroundResource(R.color.colorOrderWaiting)
                }
                ORDER_STATUS_COOKING -> {
                    tvOrderStatus.text = context.getText(R.string.orderCooking)
                    layoutOrderStatus.setBackgroundResource(R.color.colorOrderCooking)
                }
                ORDER_STATUS_IN_TRANSIT -> {
                    tvOrderStatus.text = context.getText(R.string.orderShipping)
                    layoutOrderStatus.setBackgroundResource(R.color.colorOrderShipping)
                }
                ORDER_STATUS_SUCCESS -> {
                    tvOrderStatus.text = context.getText(R.string.orderSuccess)
                    layoutOrderStatus.setBackgroundResource(R.color.colorOrderSuccess)
                }
                ORDER_STATUS_CANCEL -> {
                    tvOrderStatus.text = context.getText(R.string.orderCancel)
                    layoutOrderStatus.setBackgroundResource(R.color.colorOrderCancel)
                }
            }
        }
    }
}