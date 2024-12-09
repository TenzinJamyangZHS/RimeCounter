package com.rimetech.rimecounter.ui.activities

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.databinding.ActivityMainBinding
import com.rimetech.rimecounter.ui.fragments.ArchiveFragment
import com.rimetech.rimecounter.ui.fragments.FavorFragment
import com.rimetech.rimecounter.ui.fragments.HistoryFragment
import com.rimetech.rimecounter.ui.fragments.HomeFragment
import com.rimetech.rimecounter.ui.fragments.LockFragment
import com.rimetech.rimecounter.ui.fragments.SearchFragment
import com.rimetech.rimecounter.ui.fragments.SettingsFragment
import com.rimetech.rimecounter.utils.ARCHIVE
import com.rimetech.rimecounter.utils.FAVOR
import com.rimetech.rimecounter.utils.HISTORY
import com.rimetech.rimecounter.utils.HOME
import com.rimetech.rimecounter.utils.LOCK
import com.rimetech.rimecounter.utils.POS_BOTTOM
import com.rimetech.rimecounter.utils.SEARCH
import com.rimetech.rimecounter.utils.SETTINGS
import com.rimetech.rimecounter.utils.clickWithAnimation
import com.rimetech.rimecounter.utils.createCustomTabView
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.getNavigationBarHeight
import com.rimetech.rimecounter.utils.longClickWithAnimation
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setOnTabDoubleClickListener
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.utils.showOrHideOnEnd
import com.rimetech.rimecounter.utils.tabIconOutLineList
import com.rimetech.rimecounter.utils.tabIconOutlineRoundList
import com.rimetech.rimecounter.utils.tabIconRoundList
import com.rimetech.rimecounter.utils.tabIconSharpList
import com.rimetech.rimecounter.utils.tabNameList
import com.rimetech.rimecounter.viewmodels.SettingsViewModel

class MainActivity : AppCompatActivity() {
    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var settingsViewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mainBinding.root)

        supportActionBar?.hide()

        settingsViewModel = (application as RimeCounter).settingsViewModel
        mainBinding.lifecycleOwner = this
        mainBinding.settingsViewModel = settingsViewModel
        setBlurView()
        setMarginBottom()
        setViewBackground()
        setPagerAdapter()
        setPagerListener()
        attachPagerTab()
        setTabLength()
        setTabListener()
        showOrHideBlurView()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        settingsViewModel.setTabSize()
        settingsViewModel.setScreenWidth()
        settingsViewModel.setListWidth()
    }


    private fun setBlurView() {
        settingsViewModel.isBlur.observe(this) { isBlur ->
            settingsViewModel.isMonet.observe(this) { isMonet ->
                mainBinding.blurView.apply {
                    setPaintBackground(
                        if (isBlur) 155 else 255,
                        this@MainActivity.dpToPx(16f),
                        ContextCompat.getColor(
                            this@MainActivity,
                            if (isMonet) R.color.color_layer2_monet else R.color.color_layer2
                        )
                    )
                    if (isBlur) setBlur(this@MainActivity, 18f) else setBlurEnabled(false)
                }
            }
        }
    }

    private fun setMarginBottom() {
        mainBinding.blurView.setMargin(getNavigationBarHeight(this), POS_BOTTOM)
    }

    private fun setViewBackground() {
        settingsViewModel.isMonet.observe(this) { isMonet ->
            mainBinding.mainRoot.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    if (isMonet) R.color.color_root_monet else R.color.color_root
                )
            )
        }
    }

    private fun setPagerAdapter() {
        mainBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 7

            override fun createFragment(position: Int): Fragment {
                return when (tabNameList[position]) {
                    HOME -> HomeFragment()
                    SEARCH -> SearchFragment()
                    FAVOR -> FavorFragment()
                    HISTORY -> HistoryFragment()
                    SETTINGS -> SettingsFragment()
                    ARCHIVE -> ArchiveFragment()
                    LOCK -> LockFragment()
                    else -> throw IllegalArgumentException("No Tab Fragment Found!")
                }
            }
        }
    }

    private fun setPagerListener() {
        mainBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                settingsViewModel.setTabPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                settingsViewModel.setIsListEnd(false)
            }
        })
    }

    private fun setTabListener() {
        mainBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.clickWithAnimation()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.view?.clickWithAnimation()
            }

        })
        mainBinding.tabLayout.setOnTabDoubleClickListener { tab ->
            when (tab.tag) {
               HOME, ARCHIVE, LOCK, FAVOR -> {
                    settingsViewModel.setIsListEnd(true)
                }
            }

        }

    }

    private fun setTabLength() {
        settingsViewModel.tabPosition.observe(this) { position ->
            settingsViewModel.tabSize.observe(this) { size ->
                if (size < 7) {
                    if (size > 3) {
                        if (position < size) {
                            for (it in 0 until size) {
                                mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                    View.VISIBLE
                            }
                            for (it in size until 7) {
                                mainBinding.tabLayout.getTabAt(it)?.view?.visibility = View.GONE
                            }
                        } else {
                            for (it in 0 until size) {
                                mainBinding.tabLayout.getTabAt(it)?.view?.visibility = View.GONE
                            }
                            for (it in size until 7) {
                                mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                    View.VISIBLE
                            }
                        }
                    } else if (size == 3) {
                        when (position) {
                            in 0 until 3 -> {
                                for (it in 0 until 3) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.VISIBLE
                                }
                                for (it in 3 until 7) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                            }

                            in 3 until 6 -> {
                                for (it in 0 until 3) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                                for (it in 3 until 6) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.VISIBLE
                                }
                                mainBinding.tabLayout.getTabAt(6)?.view?.visibility =
                                    View.GONE
                            }

                            6 -> {
                                for (it in 0 until 6) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                                mainBinding.tabLayout.getTabAt(6)?.view?.visibility =
                                    View.VISIBLE

                            }
                        }
                    } else if (size == 2) {
                        when (position) {
                            in 0 until 2 -> {
                                for (it in 0 until 2) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.VISIBLE
                                }
                                for (it in 2 until 7) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                            }

                            in 2 until 4 -> {
                                for (it in 2 until 4) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.VISIBLE
                                }
                                for (it in 4 until 7) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                                for (it in 0 until 2) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                            }

                            in 4 until 6 -> {
                                for (it in 0 until 4) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                                for (it in 4 until 6) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.VISIBLE
                                }
                                mainBinding.tabLayout.getTabAt(6)?.view?.visibility =
                                    View.GONE

                            }

                            6 -> {
                                for (it in 0 until 6) {
                                    mainBinding.tabLayout.getTabAt(it)?.view?.visibility =
                                        View.GONE
                                }
                                mainBinding.tabLayout.getTabAt(6)?.view?.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                } else {
                    for (it in 0 until 7) {
                        mainBinding.tabLayout.getTabAt(it)?.view?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun attachPagerTab() {
        TabLayoutMediator(mainBinding.tabLayout, mainBinding.viewPager) { tab, position ->
            settingsViewModel.tabStyle.observe(this) { style ->
                val name = settingsViewModel.tabRange.value!![position]

                val iconId = when (style) {
                R.id.icon_round -> tabIconRoundList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                R.id.icon_sharp -> tabIconSharpList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                R.id.icon_outline -> tabIconOutLineList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                R.id.icon_outline_round -> tabIconOutlineRoundList.find { it.first == name }?.second?:throw IllegalArgumentException("Cannot find the tab icon!")
                else -> throw IllegalArgumentException("No such a icon style!")
            }

                settingsViewModel.tabColor.observe(this) { colorId ->
                    tab.setCustomView(
                        createCustomTabView(
                            R.layout.tab_icon,
                            R.id.tab_icon_image,
                            iconId,
                            colorId,
                            this,
                            settingsViewModel.colorsMap
                        )
                    )

                    val colorTemp: Int
                    val colorToUse: Int
                    if (colorId == R.id.color_default) {
                        colorToUse = ContextCompat.getColor(this, R.color.color_default)
                    } else {
                        colorTemp = ContextCompat.getColor(
                            this,
                            settingsViewModel.colorsMap[colorId]!!
                        )
                        colorToUse = Color.argb(
                            (0.5f * 255).toInt(),
                            Color.red(colorTemp),
                            Color.green(colorTemp),
                            Color.blue(colorTemp)
                        )
                    }
                    mainBinding.tabLayout.setSelectedTabIndicatorColor(colorToUse)
                }
            }
            tab.tag = tabNameList[position]
            tab.view.setOnClickListener {
                mainBinding.viewPager.setCurrentItem(tab.position, false)
                it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
            tab.view.setOnLongClickListener {
                mainBinding.viewPager.setCurrentItem(tab.position, false)
                it.longClickWithAnimation()
                it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                true
            }

        }.attach()
    }

    private fun showOrHideBlurView() {
        settingsViewModel.isListEnd.observe(this) { isEnd ->
            mainBinding.blurView.showOrHideOnEnd(isEnd)
        }
    }




    fun onTabStyleChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.setTabStyle(view.id)
    }

    fun onTabColorChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.setTabColor(view.id)
    }

    fun onCounterShapeChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.setCounterShape(view.id)
    }

    fun onCounterBackgroundChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.setCounterBackground(view.id)
    }

    fun onThemeChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.setThemeId(view.id)
        AppCompatDelegate.setDefaultNightMode(settingsViewModel.themeList.find { it.first == view.id }?.second!!)
    }

    fun onSettingsApplied(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        settingsViewModel.saveSettings()
        setPagerAdapter()
        this.recreate()
    }

}