package com.csc48.deliverycoffeeshop.ui.fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.CartAdapter
import com.csc48.deliverycoffeeshop.model.ProductModel
import kotlinx.android.synthetic.main.fragment_order_cart_dialog.*
import java.util.*

class OrderCartDialogFragment : DialogFragment() {
    private var cart: List<ProductModel> = listOf()
    private val adapter = CartAdapter()
    private var callback: OrderCartListener? = null

    interface OrderCartListener {
        fun onClearCart()
        fun onOrderEditor()
    }

    fun setOrderCartListener(callback: OrderCartListener) {
        this.callback = callback
    }

    companion object {
        const val TAG = "OrderCartDialogFragment"
        fun newInstance(cart: List<ProductModel>): OrderCartDialogFragment {
            return OrderCartDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("CART", cart as ArrayList)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        cart = arguments?.getParcelableArrayList("CART") ?: listOf()
        return inflater.inflate(R.layout.fragment_order_cart_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvCart.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        adapter.mData = cart
        rvCart.adapter = adapter

        btnCreateOrder.setOnClickListener {
            callback?.onOrderEditor()
            dialog.dismiss()
        }

        btnClearCart.setOnClickListener {
            callback?.onClearCart()
            dialog.dismiss()
        }
    }


}
