package com.biggemot.largetextparser.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.biggemot.largetextparser.databinding.ItemResultsListBinding
import timber.log.Timber

class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.Holder>() {

    private val items = mutableListOf<String>()
    private var onItemClick: ((Int) -> Unit)? = null
    private var selectedItems = setOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemResultsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            binding.root.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], selectedItems.contains(position))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<String>) {
        // for optimization, we assume that items are always added to the end of the list
        if (newItems.size - items.size == 1) {
            items.add(newItems.last())
            notifyItemInserted(items.size - 1)
        } else {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }
    }

    fun setOnItemClick(action: ((Int) -> Unit)?) {
        onItemClick = action
    }

    fun setSelectedItems(positions: Set<Int>) {
        // get symmetrical diff
        val updatePositions = selectedItems.toMutableSet().apply {
            removeAll(positions)
            addAll(positions.toMutableSet().apply { removeAll(selectedItems) })
        }
        selectedItems = positions
        updatePositions.forEach {
            notifyItemChanged(it)
        }
    }

    class Holder(val binding: ItemResultsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isSelected: Boolean) {
            binding.mainTextView.text = item
            binding.root.isSelected = isSelected
        }
    }
}