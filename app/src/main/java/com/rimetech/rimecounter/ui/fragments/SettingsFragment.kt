package com.rimetech.rimecounter.ui.fragments

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.FragmentSettingsBinding
import com.rimetech.rimecounter.databinding.TabIconBinding
import com.rimetech.rimecounter.utils.POS_ALL
import com.rimetech.rimecounter.utils.POS_TOP
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.utils.tabIconOutLineList
import com.rimetech.rimecounter.utils.tabIconOutlineRoundList
import com.rimetech.rimecounter.utils.tabIconRoundList
import com.rimetech.rimecounter.utils.tabIconSharpList
import com.rimetech.rimecounter.viewmodels.SettingsViewModel
import java.util.Collections

@Suppress("DEPRECATION")
class SettingsFragment: Fragment() {
    private lateinit var settingsViewModel: SettingsViewModel
    private val settingsBinding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }
    private lateinit var tabAdapter: TabAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        settingsBinding.lifecycleOwner = viewLifecycleOwner
        settingsBinding.settingsViewModel = settingsViewModel
        setTopMargin()
        setBlurView()
        setViewBackground()
        setMonet()
        setTabSorter()
        return settingsBinding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setTopMargin()
    }

    private fun setTopMargin() {
        settingsBinding.blurView.setMargin(getStatusBarHeight(requireActivity()), POS_TOP)
        settingsBinding.tabLayout.setMargin(
            getStatusBarHeight(requireActivity()) + requireActivity().dpToPx(88f).toInt(),
            POS_TOP
        )
    }

    private fun setBlurView() {
        settingsViewModel.isBlur.observe(viewLifecycleOwner) { isBlur ->
            settingsViewModel.tabColor.observe(viewLifecycleOwner) { colorId ->
                settingsBinding.blurView.setPaintBackground(
                    if (isBlur) 155 else 255,
                    requireActivity().dpToPx(12f),
                    ContextCompat.getColor(
                        requireActivity(),
                        settingsViewModel.colorsMap[colorId] ?: R.color.color_default
                    )
                )
                if (isBlur) {
                    settingsBinding.blurView.setBlur(requireActivity(), 18f)
                } else {
                    settingsBinding.blurView.setBlurEnabled(false)
                }
            }
        }
    }

    private fun setViewBackground() {
        settingsViewModel.isMonet.observe(viewLifecycleOwner) { isMonet ->
            val colorLayer1 = ContextCompat.getColor(
                requireActivity(),
                if (isMonet) R.color.color_layer1_monet else R.color.color_layer1
            )
            colorLayer1.let {
                val backgroundTab = settingsBinding.tabLayout.background
                if (backgroundTab is GradientDrawable) {
                    backgroundTab.setColor(it)
                }
                val backgroundTheme = settingsBinding.themeLayout.background
                if (backgroundTheme is GradientDrawable) {
                    backgroundTheme.setColor(it)
                }
                val backgroundCounter = settingsBinding.counterLayout.background
                if (backgroundCounter is GradientDrawable) {
                    backgroundCounter.setColor(it)
                }
            }
            val colorRoot = ContextCompat.getColor(
                requireActivity(),
                if (isMonet) R.color.color_root_monet else R.color.color_root
            )
            colorRoot.let {
                settingsBinding.settingsRoot.setBackgroundColor(it)
            }
        }
    }

    private fun setMonet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            settingsBinding.monetCheckBox.apply {
                text = context.getString(R.string.monet_need_android_12)
                isEnabled = false
            }
        } else {
            settingsBinding.monetCheckBox.apply {
                isEnabled = true
                val drawable =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.monet_24)
                val isDark =
                    (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                val drawableColor =
                    if (isDark) android.R.color.system_accent1_700 else android.R.color.system_accent1_200
                drawable?.setTint(ContextCompat.getColor(requireActivity(), drawableColor))
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            }
        }
    }


    inner class TabAdapter(private val tabList: MutableList<String>) :
        RecyclerView.Adapter<TabAdapter.TabHolder>() {
        inner class TabHolder(val binding: TabIconBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabHolder {
            val binding = TabIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TabHolder(binding)
        }

        override fun getItemCount(): Int = tabList.size

        override fun onBindViewHolder(holder: TabHolder, position: Int) {
            holder.binding.tabIconImage.apply {
                settingsViewModel.tabStyle.observe(viewLifecycleOwner) { style ->

                    val name = tabList[position]
                    val iconId = when (style) {
                        R.id.icon_round -> tabIconRoundList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                        R.id.icon_sharp -> tabIconSharpList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                        R.id.icon_outline -> tabIconOutLineList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                        R.id.icon_outline_round -> tabIconOutlineRoundList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                        else -> throw IllegalArgumentException("No such a icon style!")
                    }
                    setImageResource(iconId)
                    settingsViewModel.tabColor.observe(viewLifecycleOwner) { colorId ->
                        if (colorId != R.id.color_default) {
                            setColorFilter(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    settingsViewModel.colorsMap[colorId]!!
                                )
                            )
                        } else {
                            clearColorFilter()
                        }
                    }
                    elevation = requireActivity().dpToPx(2f)
                    setMargin(requireActivity().dpToPx(4f).toInt(), POS_ALL)
                    settingsViewModel.isMonet.observe(viewLifecycleOwner) { monet ->
                        setPaintBackground(
                            255,
                            requireActivity().dpToPx(16f),
                            ContextCompat.getColor(
                                requireActivity(),
                                if (monet) R.color.color_layer2_monet else R.color.color_layer2
                            )
                        )
                    }
                }
            }
        }

        fun swapItems(
            containerToSwap: MutableList<String>,
            fromPosition: Int,
            toPosition: Int
        ) {
            Collections.swap(containerToSwap, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    private val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                tabAdapter.swapItems(
                    settingsViewModel.tabRange.value!!,
                    fromPos,
                    toPos
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

        }

    private fun setTabSorter() {
        tabAdapter = TabAdapter(settingsViewModel.tabRange.value!!)
        settingsBinding.tabRecyclerView.adapter = tabAdapter
        settingsBinding.tabRecyclerView.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(settingsBinding.tabRecyclerView)
    }



}