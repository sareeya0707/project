package com.csc48.deliverycoffeeshop.ui.fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.csc48.deliverycoffeeshop.R
import kotlinx.android.synthetic.main.fragment_customer_console_dialog.*

class CustomerConsoleDialogFragment : DialogFragment() {
    private val TAG = CustomerConsoleDialogFragment::class.java.simpleName
    private var callback: ConsoleListener? = null

    interface ConsoleListener {
        fun onCartManage()
        fun onOrderManage()
        fun onEditProfile()
        fun onLogout()
    }

    fun setConsoleListener(callback: ConsoleListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_console_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCartManage.setOnClickListener {
            callback?.onCartManage()
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
