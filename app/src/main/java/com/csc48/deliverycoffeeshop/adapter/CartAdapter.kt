package com.csc48.deliverycoffeeshop.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.ProductModel

class CartAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mData: List<ProductModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindViews(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val imvProductImage = itemView.findViewById<ImageView>(R.id.imvProductImage)
        private val tvProductName = itemView.findViewById<TextView>(R.id.tvProductName)
        private val tvProductQuantity = itemView.findViewById<TextView>(R.id.tvProductQuantity)
        private val btnRemoveItem = itemView.findViewById<ImageView>(R.id.btnRemoveItem)

        init {
            btnRemoveItem.setOnClickListener {
                mData = mData - mData[adapterPosition]
                notifyDataSetChanged()
            }
        }

        fun bindViews(productModel: ProductModel) {
            Glide.with(context)
                    .load(productModel.image ?: "")
                    .into(imvProductImage)

            tvProductName.text = productModel.name ?: ""

            tvProductQuantity.text = productModel.quantity.toString()
        }
    }
}