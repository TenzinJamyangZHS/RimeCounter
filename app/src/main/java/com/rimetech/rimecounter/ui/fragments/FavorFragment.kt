package com.rimetech.rimecounter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.FragmentFavorBinding
import com.rimetech.rimecounter.utils.LIKED_LIST
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

class FavorFragment: ListFragment() {
    private val favoredBinding by lazy { FragmentFavorBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        favoredBinding.lifecycleOwner = viewLifecycleOwner
        favoredBinding.listCounterViewModel= listCounterViewModel
        setLayoutManager(favoredBinding.recyclerview)
        setTopDecoration(favoredBinding.recyclerview)
        setListListener(favoredBinding.recyclerview)
        setAdapter(favoredBinding.recyclerview, LIKED_LIST,favoredBinding.emptyText)
        return favoredBinding.root
    }
}