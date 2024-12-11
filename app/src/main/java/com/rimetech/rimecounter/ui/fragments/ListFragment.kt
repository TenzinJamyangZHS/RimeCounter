package com.rimetech.rimecounter.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.utils.ARCHIVED_LIST
import com.rimetech.rimecounter.utils.HISTORY_LIST
import com.rimetech.rimecounter.utils.HOME_LIST
import com.rimetech.rimecounter.utils.LIKED_LIST
import com.rimetech.rimecounter.utils.LOCKED_LIST
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.removeSpecificItemDecoration
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel
import com.rimetech.rimecounter.viewmodels.SettingsViewModel

@Suppress("DEPRECATION")
abstract class ListFragment: Fragment() {
    private lateinit var itemTouchHelper: ItemTouchHelper
    lateinit var listCounterViewModel: ListCounterViewModel
    lateinit var settingsViewModel: SettingsViewModel
    lateinit var listLayoutManager: GridLayoutManager

    inner class TopSpaceDecoration(private val topMargin: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            settingsViewModel.listWidth.observe(viewLifecycleOwner) { width ->
                val position = parent.getChildAdapterPosition(view)
                if (position < width) {
                    outRect.top = topMargin

                }
            }

        }

    }

    fun setTopDecoration(recyclerView: RecyclerView) {
        recyclerView.removeSpecificItemDecoration(TopSpaceDecoration::class.java)
        recyclerView.addItemDecoration(
            TopSpaceDecoration(
                getStatusBarHeight(
                    requireActivity()
                )
            )
        )
    }

    fun setLayoutManager(recyclerView: RecyclerView) {
        settingsViewModel.listWidth.observe(viewLifecycleOwner) { width ->
            listLayoutManager =
                GridLayoutManager(requireActivity(), width, GridLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = listLayoutManager
        }
    }

    fun setListListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        settingsViewModel.setIsListEnd(listLayoutManager.findFirstCompletelyVisibleItemPosition() > 0)
                    } else {
                        settingsViewModel.setIsListEnd(false)
                    }
                }
            }
        })
    }

    fun setAdapter(recyclerView: RecyclerView, listType: Int,emptyText: TextView) {
        listCounterViewModel.getCounters().observe(viewLifecycleOwner) { counters ->
            when (listType) {
                HOME_LIST -> {
                    val homeList =
                        counters.filter { !it.isLocked && !it.isArchived }.toMutableList()
                    listCounterViewModel.setCounterList(homeList)
                    listCounterViewModel.counterListHomeAdapter.updateList(homeList)
                    checkEmpty(HOME_LIST,emptyText, recyclerView)
                }

                LOCKED_LIST -> {
                    val lockedList = counters.filter { it.isLocked }.toMutableList()
                    listCounterViewModel.setCounterListLocked(lockedList)
                    listCounterViewModel.counterListLockedAdapter.updateList(lockedList)
                    checkEmpty(LOCKED_LIST,emptyText, recyclerView)
                }

                LIKED_LIST -> {
                    val favoredList = counters.filter { it.isFavored && !it.isLocked && !it.isArchived}.toMutableList()
                    listCounterViewModel.setCounterListLiked(favoredList)
                    listCounterViewModel.counterListLikedAdapter.updateList(favoredList)
                    checkEmpty(LIKED_LIST,emptyText, recyclerView)
                }

                ARCHIVED_LIST -> {
                    val archivedList = counters.filter { it.isArchived && !it.isLocked}.toMutableList()
                    listCounterViewModel.setCounterListArchived(archivedList)
                    listCounterViewModel.counterListArchivedAdapter.updateList(archivedList)
                    checkEmpty(ARCHIVED_LIST,emptyText, recyclerView)
                }
            }
            setItemTouchHelper(recyclerView, listType)
        }
    }

    private fun setItemTouchHelper(recyclerView: RecyclerView, listType: Int) {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            @SuppressLint("ResourceAsColor")
            private val background = ColorDrawable(R.color.color_default)
            private val icon = ContextCompat.getDrawable(
                requireContext(), when(listType){
                    HOME_LIST->R.drawable.sharp_archive_48
                    LIKED_LIST->R.drawable.outline_favorite_48
                    ARCHIVED_LIST->R.drawable.sharp_unarchive_48
                    LOCKED_LIST->R.drawable.sharp_lock_open_right_40
                    else-> throw IllegalArgumentException("Unknown Swipe Action!")
                }
            )


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val swipeFlags = ItemTouchHelper.RIGHT
                val dragFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
                }
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.RIGHT) {

                    when (listType) {
                        HOME_LIST -> {
                            val counter =
                                listCounterViewModel.counterListHomeAdapter.getCounterFromPosition(
                                    position
                                )
                            counter.isArchived = true
                            listCounterViewModel.updateCounter(counter)

                            Toast.makeText(requireActivity(),if (counter.isArchived)"Archived!" else "Unarchived!",
                                Toast.LENGTH_SHORT).show()
                        }

                        LOCKED_LIST -> {
                            val counter =
                                listCounterViewModel.counterListLockedAdapter.getCounterFromPosition(
                                    position
                                )
                            counter.isLocked = false
                            listCounterViewModel.updateCounter(counter)

                            Toast.makeText(requireActivity(),if (counter.isLocked)"Locked!" else "Unlocked!",Toast.LENGTH_SHORT).show()
                        }

                        LIKED_LIST -> {
                            val counter =
                                listCounterViewModel.counterListLikedAdapter.getCounterFromPosition(
                                    position
                                )
                            counter.isFavored = false
                            listCounterViewModel.updateCounter(counter)

                            Toast.makeText(requireActivity(),if (counter.isFavored)"Liked!" else "Unliked!",Toast.LENGTH_SHORT).show()
                        }

                        ARCHIVED_LIST -> {
                            val counter =
                                listCounterViewModel.counterListArchivedAdapter.getCounterFromPosition(
                                    position
                                )
                            counter.isArchived = false
                            listCounterViewModel.updateCounter(counter)
                            Toast.makeText(requireActivity(),if (counter.isArchived)"Archived!" else "Unarchived!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                }

            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView

                    background.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt(), itemView.bottom
                    )
                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + it.intrinsicWidth

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }



            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun checkEmpty(listType: Int, emptyText:TextView, recyclerView: RecyclerView){
        var isEmpty= true
        when(listType){
            HOME_LIST->{
                isEmpty = listCounterViewModel.counterListHome.value.isNullOrEmpty()
            }
            LIKED_LIST->{
                isEmpty = listCounterViewModel.counterListLiked.value.isNullOrEmpty()
            }
            ARCHIVED_LIST->{
                isEmpty = listCounterViewModel.counterListArchived.value.isNullOrEmpty()
            }
            LOCKED_LIST->{
                isEmpty = listCounterViewModel.counterListLocked.value.isNullOrEmpty()
            }
        }
        if (isEmpty) {
            emptyText.visibility=View.VISIBLE
            recyclerView.visibility =View.GONE
        } else {
            emptyText.visibility=View.GONE
            recyclerView.visibility =View.VISIBLE
        }
    }
}