package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.ProductsAdapter
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
    private val adapter = ProductsAdapter()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProductManagementViewModel::class.java)
        setContentView(R.layout.activity_product_management)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = adapter

        btnAddProduct.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProductEditorFragment(), "ProductEditorFragment")
                .addToBackStack(null)
                .commit()
        }

        mViewModel.products.observe(this, Observer { products ->
            adapter.mData = products ?: listOf()
            adapter.notifyDataSetChanged()
        })
    }
}
