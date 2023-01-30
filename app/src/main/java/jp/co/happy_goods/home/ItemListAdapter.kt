package jp.co.happy_goods.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.co.happy_goods.data.ItemListModel
import jp.co.happy_goods.databinding.ItemListBinding
import java.text.SimpleDateFormat
import java.util.*

class ItemListAdapter(val onItemClicked:(ItemListModel) -> Unit): ListAdapter<ItemListModel, ItemListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemListModel: ItemListModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(itemListModel.createdAt)

            binding.titleTextView.text = itemListModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = itemListModel.price

            if(itemListModel.imageUrl.isNotEmpty()){
                Glide.with(binding.thumbnailImageView)
                    .load(itemListModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            binding.root.setOnClickListener {
                onItemClicked(itemListModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ItemListModel>() {
            override fun areItemsTheSame(oldItem: ItemListModel, newItem: ItemListModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ItemListModel, newItem: ItemListModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}