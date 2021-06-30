package dev.chau.testandroidbilling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import dev.chau.testandroidbilling.databinding.ItemProductBinding

class ProductAdapter(val action: (SkuDetails) -> Unit) :
    ListAdapter<SkuDetails, ProductAdapter.ProductHolder>(ProductCallback()) {
    inner class ProductHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skuDetails: SkuDetails) {
            binding.txtDes.text = skuDetails.description
            binding.txtPrice.text = skuDetails.price
            binding.txtTitle.text = skuDetails.title
            binding.item.setOnClickListener { action(skuDetails) }
        }
    }

    class ProductCallback : DiffUtil.ItemCallback<SkuDetails>() {
        override fun areItemsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductHolder(ItemProductBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(getItem(position))
    }

}