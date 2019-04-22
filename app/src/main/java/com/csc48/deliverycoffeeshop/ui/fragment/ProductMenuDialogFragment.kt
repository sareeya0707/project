package com.csc48.deliverycoffeeshop.ui.fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.ProductModel
import kotlinx.android.synthetic.main.fragment_product_menu_dialog.*

class ProductMenuDialogFragment : DialogFragment() {
    private var product: ProductModel? = null

    

    companion object {
        const val TAG = "ProductMenuDialogFragment"
        fun newInstance(productModel: ProductModel): ProductMenuDialogFragment {
            return ProductMenuDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("PRODUCT", productModel)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        product = arguments?.getParcelable("PRODUCT")
        return inflater.inflate(R.layout.fragment_product_menu_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnEdit.setOnClickListener {

        }

        btnDelete.setOnClickListener {

        }
    }
}
