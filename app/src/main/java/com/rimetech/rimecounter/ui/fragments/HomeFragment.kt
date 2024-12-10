package com.rimetech.rimecounter.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.FragmentHomeBinding
import com.rimetech.rimecounter.utils.HOME_LIST
import com.rimetech.rimecounter.utils.POS_BOTTOM
import com.rimetech.rimecounter.utils.POS_LEFT
import com.rimetech.rimecounter.utils.clickWithAnimation
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.getNavigationBarHeight
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setDragOnYAxis
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

class HomeFragment : ListFragment() {
    private val homeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        homeBinding.lifecycleOwner = viewLifecycleOwner
        homeBinding.listCounterViewModel = listCounterViewModel
        setAddButtonMargin()
        setLayoutManager(homeBinding.recyclerview)
        setAdapter(homeBinding.recyclerview, HOME_LIST, homeBinding.emptyText)
        setTopDecoration(homeBinding.recyclerview)
        setListListener(homeBinding.recyclerview)
        setAddButton()
        return homeBinding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setAddButtonMargin()
    }

    private fun setAddButtonMargin() {
        homeBinding.blurView.apply {
            setMargin(
                getNavigationBarHeight(requireActivity()) + requireActivity().dpToPx(100f).toInt(),
                POS_BOTTOM
            )
            settingsViewModel.screenWidth.observe(viewLifecycleOwner) { width ->
                setMargin(width - requireActivity().dpToPx(100f).toInt(), POS_LEFT)
            }
        }
    }

    private fun setAddButton() {
        settingsViewModel.isMonet.observe(viewLifecycleOwner) { monet ->
            val color = ContextCompat.getColor(
                requireActivity(),
                if (monet) R.color.color_add_button_monet else R.color.color_add_button
            )
            color.let {
                val drawableAdd = homeBinding.addCounterButton.drawable
                val tintedDrawable = DrawableCompat.wrap(drawableAdd).mutate()
                DrawableCompat.setTint(tintedDrawable, color)
                homeBinding.addCounterButton.setImageDrawable(tintedDrawable)
            }
            settingsViewModel.isBlur.observe(viewLifecycleOwner) { blur ->
                homeBinding.blurView.setPaintBackground(
                    if (blur) 125 else 255,
                    requireActivity().dpToPx(35f),
                    ContextCompat.getColor(requireActivity(),if (monet) R.color.color_layer2_monet else R.color.color_layer2)
                )
                if (blur) homeBinding.blurView.setBlur(
                    requireActivity(),
                    18f
                ) else homeBinding.blurView.setBlurEnabled(false)
            }
        }
        homeBinding.addCounterButton.apply {
            isClickable = false
        }
        homeBinding.blurView.setDragOnYAxis()
        homeBinding.blurView.clickWithAnimation()
    }

}