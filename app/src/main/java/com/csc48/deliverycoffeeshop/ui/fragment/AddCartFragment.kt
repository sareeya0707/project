package com.csc48.deliverycoffeeshop.ui.fragment


import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.ProductModel
import kotlinx.android.synthetic.main.fragment_add_cart.*

class AddCartFragment : DialogFragment() {
    private val TAG = CustomerConsoleDialogFragment::class.java.simpleName
    private var callback: AddCartListener? = null
    private var product: ProductModel? = null

    interface AddCartListener {
        fun onAddCart(productModel: ProductModel)
    }

    fun setAddCartListener(callback: AddCartListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        product = arguments?.getParcelable("PRODUCT")
        return inflater.inflate(R.layout.fragment_add_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product?.apply {
            Glide.with(this@AddCartFragment)
                .load(image ?: "")
                .into(imvProductImage)
            tvProductName.text = name ?: ""
        }

        btnAddProduct.setOnClickListener {
            addToCart()
        }

        edtProductQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val productPrice = product?.price ?: 0.0
                val text = edtProductQuantity.text.toString()
                var quantity = 0
                if (!text.isBlank()) {
                    quantity = Integer.parseInt(text)
                }
                product!!.quantity = quantity
                val netPrice = productPrice * quantity
                tvNetPrice.text = netPrice.toString()
            }
        })

    }

    private fun addToCart() {
        val quantity = edtProductQuantity.text.toString()
        if (quantity.isNotBlank() && quantity != "0") {
            layoutProductQuantity.error = null
            if (product != null) {
                Log.d(TAG, "product: $product")
                callback?.onAddCart(product!!)
                dismiss()
            }
        } else {
            layoutProductQuantity.error = "กรุณากรอกจำนวน"
        }
    }

    private fun checkField(input: String, errorLayout: TextInputLayout, errorText: String): Boolean {
        return if (input.isNotBlank()) {
            errorLayout.error = null
            true
        } else {
            errorLayout.error = errorText
            false
        }
    }

}
