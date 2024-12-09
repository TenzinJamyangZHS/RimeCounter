package com.rimetech.rimecounter.ui.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.databinding.CounterListItemBinding
import com.rimetech.rimecounter.utils.colorToRColorList
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

class CounterListAdapter(
    private val counterList: MutableList<Counter>,
    private val listCounterViewModel: ListCounterViewModel
) : RecyclerView.Adapter<CounterListAdapter.CounterListHolder>() {
    private lateinit var fragmentActivity: FragmentActivity

    inner class CounterListHolder(private val holderBinding: CounterListItemBinding) :
        RecyclerView.ViewHolder(holderBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(counter: Counter) {
            holderBinding.listCounterViewModel = listCounterViewModel
            holderBinding.apply {
                listItemRootAlt.visibility = View.GONE
                listItemRoot.visibility = View.VISIBLE
                this.counter = counter
                counter.color.let { colorId ->
                    val colorValue = colorToRColorList.find { it.first == colorId }?.second
                        ?: throw IllegalArgumentException("Color not found!")
                    val colorToUse = ContextCompat.getColor(fragmentActivity, colorValue)
                    (listItemRoot.background as? GradientDrawable)?.setColor(colorToUse)
                }
                itemName.isSelected = true
                itemValue.isSelected = true
                itemValue.text = counter.currentValue.toString()
                executePendingBindings()
            }
            holderBinding.listItemRoot.apply {
                setOnLongClickListener { rootView ->
                    rootView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    rootView.visibility = View.GONE
                    holderBinding.listItemRootAlt.visibility = View.VISIBLE
                    true
                }
                setOnClickListener { view ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
            }
            holderBinding.itemFavorButton.apply {
                setImageResource(if (counter.isFavored) R.drawable.sharp_favorite_40 else R.drawable.sharp_favorite_border_40)
            }
            holderBinding.itemEditButton.apply {
                setOnClickListener { view ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterListHolder {
        val activity = parent.context as? FragmentActivity
            ?: throw IllegalArgumentException("Parent context is not a FragmentActivity")
        fragmentActivity = activity
        val binding = DataBindingUtil.inflate<CounterListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.counter_list_item, parent, false
        )
        binding.listItemRootAlt.visibility = View.GONE
        binding.listItemRoot.visibility = View.VISIBLE
        return CounterListHolder(binding)
    }

    override fun getItemCount(): Int = counterList.size

    override fun onBindViewHolder(holder: CounterListHolder, position: Int) {
        val counter = counterList[position]

        holder.bind(counter)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: MutableList<Counter>) {
        counterList.clear()
        counterList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getCounterFromPosition(position: Int): Counter {
        return if (position in 0 until counterList.size) {
            counterList[position]
        } else throw IllegalArgumentException("${position}is out of list size")
    }


}