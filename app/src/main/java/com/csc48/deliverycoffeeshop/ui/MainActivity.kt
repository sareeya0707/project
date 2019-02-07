package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.ProductsAdapter
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.viewmodel.MainViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: MainViewModel
    private val adapter = ProductsAdapter(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        adapter.setOnSelectListener(object : ProductsAdapter.OnSelectListener {
            override fun onSelectItem(productModel: ProductModel, isSelected: Boolean) {

            }
        })
        rvProducts.layoutManager = GridLayoutManager(this, 2, LinearLayout.VERTICAL, false)
        rvProducts.setHasFixedSize(true)
        rvProducts.adapter = adapter

        mViewModel.products.observe(this, Observer { products ->
            adapter.mData = products ?: listOf()
            adapter.notifyDataSetChanged()
        })

        mViewModel.user.observe(this, Observer { user ->
            val isAdmin = user?.is_admin ?: false
            if (isAdmin) {
                btnAdminConsole.visibility = View.VISIBLE
                btnCreateOrder.visibility = View.GONE
            } else {
                btnAdminConsole.visibility = View.GONE
                btnCreateOrder.visibility = View.VISIBLE
            }
        })

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getProducts()

        val isLogin = mViewModel.checkSession()
        if (isLogin) {
            btnLogin.visibility = View.GONE
            mViewModel.getUser()
        } else {
            btnLogin.visibility = View.VISIBLE
        }
    }
}
