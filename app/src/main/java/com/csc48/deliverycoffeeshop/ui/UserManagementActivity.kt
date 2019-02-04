package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.UsersAdapter
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.viewmodel.UserManagementViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_user_management.*
import javax.inject.Inject

class UserManagementActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: UserManagementViewModel
    private val adapter = UsersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserManagementViewModel::class.java)
        setContentView(R.layout.activity_user_management)

        adapter.setOnAdminChangeListener(object : UsersAdapter.OnAdminChangeListener {
            override fun onAvailableChange(userModel: UserModel) {
                mViewModel.updateUser(userModel)
            }

        })
        rvUsers.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvUsers.setHasFixedSize(true)
        rvUsers.adapter = adapter

        btnBack.setOnClickListener {
            this.finish()
        }
        mViewModel.users.observe(this, Observer { users ->
            adapter.mData = users ?: listOf()
            adapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getUsers()
    }
}
