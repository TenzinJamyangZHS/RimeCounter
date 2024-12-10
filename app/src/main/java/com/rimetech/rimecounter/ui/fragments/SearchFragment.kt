package com.rimetech.rimecounter.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.databinding.FragmentSearchBinding
import com.rimetech.rimecounter.ui.adapters.CounterListAdapter
import com.rimetech.rimecounter.utils.POS_HOR
import com.rimetech.rimecounter.utils.POS_TOP
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setDragOnYAxis
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel

class SearchFragment: ListFragment() {
    private var resultList = mutableListOf<Counter>()
    private val searchBinding by lazy { FragmentSearchBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listCounterViewModel =
            ViewModelProvider(requireActivity())[ListCounterViewModel::class.java]
        settingsViewModel = (requireActivity().application as RimeCounter).settingsViewModel
        searchBinding.lifecycleOwner = viewLifecycleOwner
        searchBinding.listCounterViewModel = listCounterViewModel
        setLayoutManager(searchBinding.recyclerview)
        setTopDecoration(searchBinding.recyclerview)
        setListListener(searchBinding.recyclerview)
        setMargin()
        setBlurView()
        return searchBinding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setMargin()
    }

    override fun onStart() {
        super.onStart()
        setSearchView()
    }

    override fun onPause() {
        super.onPause()
        resultList.clear()
        searchBinding.searchView.setQuery("", false)
    }

    private fun setMargin() {
        searchBinding.blurView.setMargin(getStatusBarHeight(requireActivity()), POS_TOP)
        searchBinding.blurView.setMargin(requireActivity().dpToPx(16f).toInt(), POS_HOR)
    }

    private fun setBlurView() {
        searchBinding.blurView.setDragOnYAxis()
        settingsViewModel.isBlur.observe(requireActivity()) { blur ->
            settingsViewModel.isMonet.observe(requireActivity()) { monet ->
                searchBinding.blurView.setPaintBackground(
                    if (blur) 155 else 255,
                    requireActivity().dpToPx(16f),
                    ContextCompat.getColor(
                        requireActivity(),
                        if (monet) R.color.color_layer2_monet else R.color.color_layer2
                    )
                )
                if (blur) searchBinding.blurView.setBlur(
                    requireActivity(),
                    18f
                ) else searchBinding.blurView.setBlurEnabled(false)
            }
        }
    }

    private fun setSearchView(){
        searchBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    listCounterViewModel.setSearchKeyWord(query)
                    listCounterViewModel.searchResults.observe(viewLifecycleOwner) { list ->
                        resultList = list
                        setSearchResultList(resultList)
                    }
                    searchBinding.searchView.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    listCounterViewModel.setSearchKeyWord(newText)
                }
                listCounterViewModel.searchResults.observe(viewLifecycleOwner) { list ->
                    resultList = list
                    setSearchResultList(list)
                }
                return true
            }
        })
    }

    private fun setSearchResultList(list:MutableList<Counter>){
        val resultList = list.filter { !it.isLocked && !it.isArchived }.toMutableList()
        val searchAdapter = CounterListAdapter(resultList,listCounterViewModel)
        searchBinding.recyclerview.adapter=searchAdapter
    }
}