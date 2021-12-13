package com.babacode.walletexpensetracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.databinding.TransactionRvItemBinding

class HomeAdepter(private val listener: OnItemClick) :
    ListAdapter<Transaction, HomeAdepter.HomeViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            TransactionRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class HomeViewHolder(private val binding: TransactionRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val transaction = getItem(position)
                    listener.OnTransactionClick(transaction)
                }
            }
        }


        fun bind(currentItem: Transaction) {

            binding.apply {

                transactionAmount.text = currentItem.amount.toString()
                transactionTitle.text = currentItem.title
                transactionType.text = currentItem.paymentType

                when(currentItem.transactionType){
                    "Expense" -> {
                        transactionMaterialCardView.setCardBackgroundColor(ContextCompat.
                                 getColor(root.context.applicationContext,R.color.expenseColor))

                    }
                    "Income" -> {
                        transactionMaterialCardView.setCardBackgroundColor(ContextCompat.
                        getColor(root.context.applicationContext,R.color.incomeColor))
                    }
                }

                when (currentItem.tag) {
                    "Other" -> {
                        imageViewForTag.setImageResource(R.drawable.other_vector)
                    }
                    "Food" -> {
                        imageViewForTag.setImageResource(R.drawable.food_vector)
                    }
                    "Shopping" -> {
                        imageViewForTag.setImageResource(R.drawable.shopping_vector)
                    }
                    "Travelling" -> {
                        imageViewForTag.setImageResource(R.drawable.traveling_vector)
                    }
                    "Entertainment" -> {
                        imageViewForTag.setImageResource(R.drawable.entertainment_vector)
                    }
                    "Health" -> {
                        imageViewForTag.setImageResource(R.drawable.medical_vector)
                    }
                    "Education" -> {
                        imageViewForTag.setImageResource(R.drawable.education_vector)
                    }
                    "Rent" -> {
                        imageViewForTag.setImageResource(R.drawable.rent_vector)
                    }
                    "bills" -> {
                        imageViewForTag.setImageResource(R.drawable.bill_vector)
                    }
                    "Gift" -> {
                        imageViewForTag.setImageResource(R.drawable.gift_vector)
                    }
                   "Investment" -> {
                        imageViewForTag.setImageResource(R.drawable.investment_vector)
                    }
                    "utils" -> {
                        imageViewForTag.setImageResource(R.drawable.utiles_vector)
                    }
                    "Salary" -> {

                    }
                    "Coupons" -> {
                        imageViewForTag.setImageResource(R.drawable.bill_vector)
                    }
                    "CashBack" -> {
                        imageViewForTag.setImageResource(R.drawable.gift_vector)
                    }
                }
            }

        }
    }
}

class DiffUtilCallBack : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

}

interface OnItemClick {
    fun OnTransactionClick(transaction: Transaction)
}