package com.csc48.deliverycoffeeshop.ui.fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.csc48.deliverycoffeeshop.R
import kotlinx.android.synthetic.main.fragment_admin_console_dialog.*

class AdminConsoleDialogFragment : DialogFragment() {
    private val TAG = AdminConsoleDialogFragment::class.java.simpleName
    private var callback: ConsoleListener? = null

    interface ConsoleListener {
        fun onAddProduct()
        fun onManageUser()
        fun onOrderManage()
        fun onEditProfile()
        fun onLogout()
    }

    fun setConsoleListener(callback: ConsoleListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_console_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddProduct.setOnClickListener {
            callback?.onAddProduct()
            dismiss()
        }

        btnUserManage.setOnClickListener {
            callback?.onManageUser()
            dismiss()
        }

        btnOrderManage.setOnClickListener {
            callback?.onOrderManage()
            dismiss()
        }

        btnUserInfo.setOnClickListener {
            callback?.onEditProfile()
            dismiss()
        }

        btnLogout.setOnClickListener {
            callback?.onLogout()
            dismiss()
        }
    }


}
