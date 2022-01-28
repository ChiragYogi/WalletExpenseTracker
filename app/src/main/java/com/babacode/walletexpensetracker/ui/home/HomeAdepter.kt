package com.babacode.walletexpensetracker.ui.home

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.TransactionRvItemBinding
import com.babacode.walletexpensetracker.ui.MainActivity
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.SettingUtils

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

            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val transaction = getItem(position)
                    listener.OnLongPress(transaction)
                }
                true
            }
        }


        fun bind(currentItem: Transaction) {

            binding.apply {

                transactionAmount.text = currentItem.amount.toString()
                transactionTitle.text = currentItem.note
                transactionType.text = currentItem.paymentType.toString()

                val currencyCode by lazy {
                    SettingUtils(binding.root.context)
                }

                currencySymbol.text = currencyCode.getCurrencyCode()
                val date = Extra.convertLongDateToStringDate(currentItem.date)
                transactionDate.text = date

                when (currentItem.transactionType) {
                    TransactionType.EXPENSE -> {
                        imageViewForTag.setColorFilter(
                            ContextCompat.getColor(
                                root.context.applicationContext,
                                R.color.expenseColor
                            )
                        )

                    }
                    TransactionType.INCOME -> {
                        imageViewForTag.setColorFilter(
                            ContextCompat.getColor(
                                root.context.applicationContext,
                                R.color.incomeColor
                            )
                        )
                    }
                }


                when (currentItem.tag) {

                    TransactionTag.OTHER -> {
                        imageViewForTag.setImageResource(R.drawable.other_vector)
                    }
                    TransactionTag.FOOD -> {
                        imageViewForTag.setImageResource(R.drawable.food_vector)
                    }
                    TransactionTag.SHOPPING -> {
                        imageViewForTag.setImageResource(R.drawable.shopping_vector)
                    }
                    TransactionTag.TRAVELLING -> {
                        imageViewForTag.setImageResource(R.drawable.traveling_vector)
                    }
                    TransactionTag.ENTERTAINMENT -> {
                        imageViewForTag.setImageResource(R.drawable.entertainment_vector)
                    }
                    TransactionTag.HEALTH -> {
                        imageViewForTag.setImageResource(R.drawable.medical_vector)
                    }
                    TransactionTag.EDUCATION -> {
                        imageViewForTag.setImageResource(R.drawable.education_vector)
                    }
                    TransactionTag.RENT -> {
                        imageViewForTag.setImageResource(R.drawable.rent_vector)
                    }
                    TransactionTag.BILLS -> {
                        imageViewForTag.setImageResource(R.drawable.bill_vector)
                    }
                    TransactionTag.GIFT -> {
                        imageViewForTag.setImageResource(R.drawable.gift_vector)
                    }
                    TransactionTag.INVESTMENT -> {
                        imageViewForTag.setImageResource(R.drawable.investment_vector)
                    }
                    TransactionTag.UTILS -> {
                        imageViewForTag.setImageResource(R.drawable.utiles_vector)
                    }
                    TransactionTag.SALARY -> {
                        imageViewForTag.setImageResource(R.drawable.money_vector)
                    }
                    TransactionTag.COUPONS -> {
                        imageViewForTag.setImageResource(R.drawable.bill_vector)
                    }
                    TransactionTag.CASHBACK -> {
                        imageViewForTag.setImageResource(R.drawable.gift_vector)
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
        fun OnLongPress(transaction: Transaction)

    }
}