package com.csc48.deliverycoffeeshop.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.UserModel

class UsersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mData: List<UserModel> = listOf()
    private var callback: OnAdminChangeListener? = null

    interface OnAdminChangeListener {
        fun onAvailableChange(userModel: UserModel)
    }

    fun setOnAdminChangeListener(callback: OnAdminChangeListener) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_manage_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindViews(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvUid = itemView.findViewById<TextView>(R.id.tvUid)
        private val tvPhone = itemView.findViewById<TextView>(R.id.tvPhone)
        private val tvUserRole = itemView.findViewById<TextView>(R.id.tvUserRole)
        private val swUserRole = itemView.findViewById<Switch>(R.id.swUserRole)

        init {
            swUserRole.setOnClickListener {
                val isChecked = swUserRole.isChecked
                val userModel = mData[adapterPosition].apply {
                    is_admin = isChecked
                }
                callback?.onAvailableChange(userModel)
            }

            swUserRole.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) tvUserRole.text = "ผู้ดูแล"
                else tvUserRole.text = "ลูกค้า"
            }
        }

        fun bindViews(userModel: UserModel) {
            val name = "${userModel.first_name ?: ""} ${userModel.last_name ?: ""}"
            tvName.text = name
            tvUid.text = userModel.uid
            tvPhone.text = userModel.phone_number

            swUserRole.isChecked = userModel.is_admin
        }
    }
}