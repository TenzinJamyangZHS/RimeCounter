package com.rimetech.rimecounter.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.databinding.FragmentHistoryBinding
import com.rimetech.rimecounter.databinding.HistoryItemItemBinding
import com.rimetech.rimecounter.databinding.HistoryListItemBinding
import com.rimetech.rimecounter.utils.colorToRColorList
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel
import java.util.Date

@Suppress("CAST_NEVER_SUCCEEDS")
class HistoryFragment : ListFragment() {
    private val historyBinding by lazy { FragmentHistoryBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        setLayoutManager(historyBinding.recyclerview)
        setTopDecoration(historyBinding.recyclerview)
        setListListener(historyBinding.recyclerview)
        historyBinding.lifecycleOwner = viewLifecycleOwner
        listCounterViewModel.getCounters().observe(viewLifecycleOwner) { counterList ->
            val historyList = counterList.filter { it.usageList.size > 0 }.toMutableList()
            setHistoryAdapter(historyList)
        }
        return historyBinding.root
    }

    private fun setHistoryAdapter(list: MutableList<Counter>) {
        historyBinding.recyclerview.adapter = HistoryAdapter(list)
    }

    inner class HistoryAdapter(private val list: MutableList<Counter>) :
        RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {
        inner class HistoryHolder(private val binding: HistoryListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(counter: Counter) {
                binding.name.text = counter.name
                binding.recyclerview.adapter = ItemAdapter(counter.usageList)
                binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
                binding.root.setPaintBackground(255,
                    requireActivity().dpToPx(12f),
                    ContextCompat.getColor(requireActivity(),
                        colorToRColorList.find { it.first == counter.color }?.second
                            ?: throw IllegalArgumentException("Counter color not found!")
                    )
                )
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            val binding = DataBindingUtil.inflate<HistoryListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.history_list_item, parent, false
            )
            return HistoryHolder(binding)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
            val counter = list[position]
            holder.bind(counter)
        }
    }

    inner class ItemAdapter(private val list: MutableList<Pair<Pair<Date, Date>, Int>>) :
        RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
        inner class ItemHolder(private val binding: HistoryItemItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(pair: Pair<Pair<Date, Date>, Int>) {
                binding.startTime.text = pair.first.first as String
                binding.endTime.text = pair.first.second as String
                binding.value.text = pair.second.toString()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val binding = DataBindingUtil.inflate<HistoryItemItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.history_item_item, parent, false
            )
            return ItemHolder(binding)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val pair = list[position]
            holder.bind(pair)
        }
    }

}