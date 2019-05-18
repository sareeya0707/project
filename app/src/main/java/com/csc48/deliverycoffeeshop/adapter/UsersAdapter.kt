package com.csc48.deliverycoffeeshop.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_ADMIN
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_CUSTOMER
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_SENDER

class UsersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mData: List<UserModel> = listOf()
    private var callback: OnAdminChangeListener? = null

    interface OnAdminChangeListener {
        fun onUserRoleChange(userModel: UserModel)
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
        private val context = itemView.context
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvUid = itemView.findViewById<TextView>(R.id.tvUid)
        private val tvPhone = itemView.findViewById<TextView>(R.id.tvPhone)
        private val btnUserRole = itemView.findViewById<Button>(R.id.btnUserRole)

        init {

            btnUserRole.setOnClickListener {
                PopupMenu(context, it).apply {
                    inflate(R.menu.user_role_menu)
                    setOnMenuItemClickListener { menu ->
                        when (menu.itemId) {
                            R.id.menuRoleCustomer -> {
                                btnUserRole.text = "ลูกค้า"
                                val userModel = mData[adapterPosition].apply {
                                    role = USER_ROLE_CUSTOMER
                                }
                                callback?.onUserRoleChange(userModel)
                            }
                            R.id.menuRoleSender -> {
                                btnUserRole.text = "ผู้ส่ง"
                                val userModel = mData[adapterPosition].apply {
                                    role = USER_ROLE_SENDER
                                }
                                callback?.onUserRoleChange(userModel)
                            }
                            R.id.menuRoleAdmin -> {
                                btnUserRole.text = "ผู้ดูแล"
                                val userModel = mData[adapterPosition].apply {
                                    role = USER_ROLE_ADMIN
                                }
                                callback?.onUserRoleChange(userModel)
                            }
                        }
                        false
                    }
                    show()
                }
            }
        }

        fun bindViews(userModel: UserModel) {
            val name = "${userModel.first_name ?: ""} ${userModel.last_name ?: ""}"
            tvName.text = name
            tvUid.text = userModel.userID
            tvPhone.text = userModel.phone_number

            btnUserRole.text = when (userModel.role) {
                USER_ROLE_SENDER -> "ผู้ส่ง"
                USER_ROLE_ADMIN -> "ผู้ดูแล"
                else -> "ลูกค้า"
            }
        }
    }
}