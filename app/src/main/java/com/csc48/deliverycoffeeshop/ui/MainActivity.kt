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
import com.csc48.deliverycoffeeshop.adapter.TopProductAdapter
import com.csc48.deliverycoffeeshop.viewmodel.MainViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: MainViewModel
    private val adapter = TopProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        rvProducts.layoutManager = GridLayoutManager(this, 1, LinearLayout.HORIZONTAL, false)
        rvProducts.setHasFixedSize(true)
        rvProducts.adapter = adapter

        // observe ตัวแปร LiveData แล้วเอาข้อมูลของมันมาใช้ทำอะไรต่างๆ
        mViewModel.products.observe(this, Observer { products ->
            if (products != null) {
                adapter.mData = products
                adapter.notifyDataSetChanged()
                mViewModel.getStatistic()
            }
        })

        mViewModel.statistics.observe(this, Observer { statistics ->
            if (statistics != null) {
                adapter.mData.forEach { s ->
                    val hasData = statistics.find { it.key == s.key }
                    if (hasData != null) s.quantity = hasData.quantity
                }
                adapter.mData = adapter.mData.sortedBy { it.quantity }.asReversed()
                adapter.notifyDataSetChanged()
            }
        })

        btnOrder.setOnClickListener {
            userPermissionCheck()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getProducts()
    }

    private fun userPermissionCheck() {
        loading.visibility = View.VISIBLE
        val isLogin = mViewModel.checkSession()
        if (isLogin) {
            mViewModel.hasUserData.observe(this, Observer { isExist ->
                if (isExist != null) {
                    loading.visibility = View.GONE
                    if (isExist) {
                        val intent = Intent(this, ProductActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, UserInfoActivity::class.java)
                        startActivity(intent)
                    }
                    mViewModel.hasUserData.value = null
                }
            })
            mViewModel.getUser()
        } else {
            loading.visibility = View.GONE
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
