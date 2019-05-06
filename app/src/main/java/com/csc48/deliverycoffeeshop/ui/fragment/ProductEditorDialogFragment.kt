package com.csc48.deliverycoffeeshop.ui.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.ProductModel
import kotlinx.android.synthetic.main.fragment_product_editor_dialog.*
import java.io.ByteArrayOutputStream

class ProductEditorDialogFragment : DialogFragment() {
    private val TAG = ProductEditorDialogFragment::class.java.simpleName
    private var REQUEST_GALLERY_CODE = 0
    private var bytes: ByteArray? = null
    private var productModel: ProductModel? = null
    private var callback: ProductEditorListener? = null

    interface ProductEditorListener {
        fun onUpdateProduct(productModel: ProductModel, bytes: ByteArray?)
        fun onClearResponse()
    }

    fun setProductEditorListener(callback: ProductEditorListener) {
        this.callback = callback
    }

    companion object {
        const val TAG = "ProductEditorDialogFragment"
        fun newInstance(productModel: ProductModel?): ProductEditorDialogFragment {
            return ProductEditorDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("PRODUCT", productModel)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_editor_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<ProductModel>("PRODUCT")?.let {
            productModel = it

            Glide.with(this@ProductEditorDialogFragment)
                    .load(it.image)
                    .into(imvProductImage)

            edtProductName.setText(it.name)
            edtProductPrice.setText(it.price.toString())
        }

        btnChooseImage.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            startActivityForResult(pickIntent, REQUEST_GALLERY_CODE)
        }

        btnClose.setOnClickListener {
            this.dismiss()
        }

        btnSaveProduct.setOnClickListener {
            saveProductData()
        }
    }

    override fun onDestroy() {
        callback?.onClearResponse()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                REQUEST_GALLERY_CODE -> {
                    val pickedImage = data?.data

                    if (pickedImage != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, pickedImage)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                        bytes = byteArrayOutputStream.toByteArray()
                    }

                    Glide.with(this@ProductEditorDialogFragment)
                            .load(bytes ?: "")
                            .into(imvProductImage)
                }
            }
    }

    private fun saveProductData() {
        val name = edtProductName.text.toString()
        val price = edtProductPrice.text.toString()

        val isNameValid = checkField(name, layoutProductName, "กรุณากรอกชื่อสินค้า")
        val isPriceValid = checkField(price, layoutProductPrice, "กรุณากรอกราคาสินค้า")

//        val isImageValid = if (bytes != null) {
//            layoutProductImage.error = null
//            true
//        } else {
//            layoutProductImage.error = "กรุณาใส่รูปสินค้า"
//            false
//        }

        if (isNameValid && isPriceValid /*&& isImageValid*/) {
            val product = productModel ?: ProductModel()
            val productModel = product.apply {
                this.name = name
                this.price = price.toDouble()
                if (this.create_at == 0L) this.create_at = System.currentTimeMillis()
                this.update_at = System.currentTimeMillis()
            }

            callback?.onUpdateProduct(productModel, bytes)
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
