package com.csc48.deliverycoffeeshop.ui


import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.viewmodel.ProductManagementViewModel
import kotlinx.android.synthetic.main.fragment_product_editor.*
import java.io.ByteArrayOutputStream

class ProductEditorFragment : Fragment() {
    private val TAG = ProductEditorFragment::class.java.simpleName
    private lateinit var mViewModel: ProductManagementViewModel
    private var REQUEST_GALLERY_CODE = 0
    private var bytes: ByteArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ProductManagementViewModel::class.java)
        return inflater.inflate(R.layout.fragment_product_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnChooseImage.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            startActivityForResult(pickIntent, REQUEST_GALLERY_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                REQUEST_GALLERY_CODE -> {
                    val pickedImage = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, pickedImage)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                    bytes = byteArrayOutputStream.toByteArray()

                    Glide.with(this@ProductEditorFragment)
                        .load(bytes)
                        .into(imvProductImage)
                }
            }
    }


}
