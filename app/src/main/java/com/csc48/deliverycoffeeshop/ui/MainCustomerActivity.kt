package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.ProductsAdapter
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.viewmodel.MainCustomerViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main_customer.*
import javax.inject.Inject

class MainCustomerActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: MainCustomerViewModel
    private val adapter = ProductsAdapter(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainCustomerViewModel::class.java)
        setContentView(R.layout.activity_main_customer)

        adapter.setOnSelectListener(object : ProductsAdapter.OnSelectListener {
            override fun onSelectItem(productModel: ProductModel) {

            }
        })
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = adapter

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
