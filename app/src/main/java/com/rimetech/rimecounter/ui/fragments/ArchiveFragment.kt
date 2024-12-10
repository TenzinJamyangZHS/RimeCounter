package com.rimetech.rimecounter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.FragmentArchiveBinding
import com.rimetech.rimecounter.utils.ARCHIVED_LIST
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

class ArchiveFragment: ListFragment() {
    private val archivedBinding by lazy { FragmentArchiveBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        archivedBinding.lifecycleOwner = viewLifecycleOwner
        archivedBinding.listCounterViewModel= listCounterViewModel
        setLayoutManager(archivedBinding.recyclerview)
        setTopDecoration(archivedBinding.recyclerview)
        setListListener(archivedBinding.recyclerview)
        setAdapter(archivedBinding.recyclerview, ARCHIVED_LIST,archivedBinding.emptyText)
        return archivedBinding.root
    }
}