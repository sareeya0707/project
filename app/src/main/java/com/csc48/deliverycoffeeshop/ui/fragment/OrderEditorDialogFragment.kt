package com.csc48.deliverycoffeeshop.ui.fragment


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.viewmodel.OrderManagementViewModel

class OrderEditorDialogFragment : Fragment() {
    private val TAG = OrderEditorDialogFragment::class.java.simpleName
    private lateinit var mViewModel: OrderManagementViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(OrderManagementViewModel::class.java)
        return inflater.inflate(R.layout.fragment_order_editor_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
