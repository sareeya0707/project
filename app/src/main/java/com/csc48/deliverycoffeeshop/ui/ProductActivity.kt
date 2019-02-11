package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.ProductsAdapter
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.ui.fragment.AddCartFragment
import com.csc48.deliverycoffeeshop.ui.fragment.AdminConsoleDialogFragment
import com.csc48.deliverycoffeeshop.ui.fragment.CustomerConsoleDialogFragment
import com.csc48.deliverycoffeeshop.ui.fragment.ProductEditorDialogFragment
import com.csc48.deliverycoffeeshop.viewmodel.ProductViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_product.*
import javax.inject.Inject

class ProductActivity : AppCompatActivity()
    , HasSupportFragmentInjector
    , AdminConsoleDialogFragment.ConsoleListener
    , CustomerConsoleDialogFragment.ConsoleListener
    , AddCartFragment.AddCartListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: ProductViewModel
    private var adapter = ProductsAdapter()
    private var productData: List<ProductModel> = listOf()
    private var cart: List<ProductModel> = listOf()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProductViewModel::class.java)
        setContentView(R.layout.activity_product)

        rvProducts.layoutManager = GridLayoutManager(this, 2, LinearLayout.VERTICAL, false)
        rvProducts.setHasFixedSize(true)

        mViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                rvProducts.adapter = null
                adapter = ProductsAdapter().apply {
                    isAdmin = user.is_admin
                    mData = productData
                }
                rvProducts.adapter = adapter

                if (user.is_admin) {
                    cart = listOf()
                    btnAdminConsole.visibility = View.VISIBLE
                    btnCustomerConsole.visibility = View.GONE
                    adapter.setOnAvailableChangeListener(object : ProductsAdapter.OnAvailableChangeListener {
                        override fun onAvailableChange(productModel: ProductModel) {
                            mViewModel.updateProduct(productModel, null)
                        }
                    })
                } else {
                    btnAdminConsole.visibility = View.GONE
                    btnCustomerConsole.visibility = View.VISIBLE
                    adapter.setOnSelectListener(object : ProductsAdapter.OnSelectListener {
                        override fun onSelectItem(productModel: ProductModel) {
                            if (supportFragmentManager.findFragmentByTag("AddCartFragment") == null) {
                                val bundle = Bundle().apply {
                                    putParcelable("PRODUCT", productModel)
                                }
                                val dialog = AddCartFragment().apply {
                                    arguments = bundle
                                    setAddCartListener(this@ProductActivity)
                                }
                                dialog.show(supportFragmentManager, "AddCartFragment")
                            }
                        }
                    })
                }
                adapter.notifyDataSetChanged()
            }
        })

        mViewModel.products.observe(this, Observer { products ->
            productData = products ?: listOf()
            adapter.mData = productData
            adapter.notifyDataSetChanged()
        })

        btnBack.setOnClickListener {
            this.finish()
        }

        btnAdminConsole.setOnClickListener {
            val dialog = AdminConsoleDialogFragment()
            dialog.setConsoleListener(this)
            dialog.show(supportFragmentManager, "AdminConsoleDialogFragment")
        }

        btnCustomerConsole.setOnClickListener {
            val dialog = CustomerConsoleDialogFragment()
            dialog.setConsoleListener(this)
            dialog.show(supportFragmentManager, "CustomerConsoleDialogFragment")
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getUser()
        mViewModel.getProducts()
    }

    override fun onAddProduct() {
        val productEditorDialogFragment = ProductEditorDialogFragment()
        productEditorDialogFragment.show(supportFragmentManager, "ProductEditorDialogFragment")
    }

    override fun onManageUser() {
        val intent = Intent(this, UserManagementActivity::class.java)
        startActivity(intent)
    }

    override fun onCartManage() {
        Toast.makeText(this, "Cart item(s): ${cart.size}", Toast.LENGTH_SHORT).show()
    }

    override fun onOrderManage() {
        val intent = Intent(this, OrderManagementActivity::class.java)
        startActivity(intent)
    }

    override fun onEditProfile() {
        val intent = Intent(this, UserInfoActivity::class.java)
        startActivity(intent)
    }

    override fun onLogout() {

    }

    override fun onAddCart(productModel: ProductModel) {
        val hasData = cart.find { it.key == productModel.key }
        cart = if (hasData == null) cart + productModel else (cart - hasData) + productModel
    }
}
