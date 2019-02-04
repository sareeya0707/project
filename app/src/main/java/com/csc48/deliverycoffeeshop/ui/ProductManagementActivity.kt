package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.ProductsAdapter
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.ui.fragment.ProductEditorDialogFragment
import com.csc48.deliverycoffeeshop.viewmodel.ProductManagementViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_product_management.*
import javax.inject.Inject

class ProductManagementActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: ProductManagementViewModel
    private val adapter = ProductsAdapter(true)

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProductManagementViewModel::class.java)
        setContentView(R.layout.activity_product_management)

        adapter.setOnAvailableChangeListener(object : ProductsAdapter.OnAvailableChangeListener {
            override fun onAvailableChange(productModel: ProductModel) {
                mViewModel.updateProduct(productModel, null)
            }
        })
        rvProducts.layoutManager = GridLayoutManager(this, 2, LinearLayout.VERTICAL, false)
        rvProducts.setHasFixedSize(true)
        rvProducts.adapter = adapter

        btnBack.setOnClickListener {
            this.finish()
        }

        btnAddProduct.setOnClickListener {
            val productEditorDialogFragment = ProductEditorDialogFragment()
            productEditorDialogFragment.show(supportFragmentManager, "ProductEditorDialogFragment")
        }

        mViewModel.products.observe(this, Observer { products ->
            adapter.mData = products ?: listOf()
            adapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getProducts()
    }
}
