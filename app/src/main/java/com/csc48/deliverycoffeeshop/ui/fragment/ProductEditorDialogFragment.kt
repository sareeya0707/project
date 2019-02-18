package com.csc48.deliverycoffeeshop.ui.fragment


import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product_editor_dialog.*
import java.io.ByteArrayOutputStream

class ProductEditorDialogFragment : DialogFragment() {
    private val TAG = ProductEditorDialogFragment::class.java.simpleName
    private lateinit var mViewModel: ProductViewModel
    private var REQUEST_GALLERY_CODE = 0
    private var bytes: ByteArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        return inflater.inflate(R.layout.fragment_product_editor_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnChooseImage.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            startActivityForResult(pickIntent, REQUEST_GALLERY_CODE)
        }

        btnSaveProduct.setOnClickListener {
            saveProductData()
        }

        mViewModel.updateProductResponse.value = null
        mViewModel.updateProductResponse.observe(this, Observer {
            it?.also { response ->
                when {
                    response.isSuccessful -> {
                        dialog.dismiss()
                    }
                    response.isCanceled -> {
                        Toast.makeText(context, "คำขอไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

    override fun onDestroy() {
        mViewModel.updateProductResponse.value = null
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
            val productModel = ProductModel().apply {
                this.name = name
                this.price = price.toDouble()
                this.create_at = System.currentTimeMillis()
                this.update_at = System.currentTimeMillis()
                this.available = false
            }
            mViewModel.updateProduct(productModel, bytes)
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
