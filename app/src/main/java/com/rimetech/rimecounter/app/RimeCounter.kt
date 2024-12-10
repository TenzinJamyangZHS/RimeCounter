package com.rimetech.rimecounter.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.rimetech.rimecounter.repositories.CounterRepository
import com.rimetech.rimecounter.ui.activities.CounterActivity
import com.rimetech.rimecounter.utils.THEME_DARK
import com.rimetech.rimecounter.utils.THEME_LIGHT
import com.rimetech.rimecounter.utils.THEME_SYSTEM
import com.rimetech.rimecounter.viewmodels.SettingsViewModel
import java.util.UUID

class RimeCounter: Application() {
    companion object {
        val counterActivityList = mutableListOf<Pair<UUID,CounterActivity>>()
    }
    val settingsViewModel by lazy {
        ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        )[SettingsViewModel::class.java]
    }
    override fun onCreate() {
        super.onCreate()
        CounterRepository.initialize(this)
        getAndSetSavedTheme()
    }

    private fun getAndSetSavedTheme() {
        val sharedPreferences = getSharedPreferences("app-settings", Context.MODE_PRIVATE)
        val themeId = sharedPreferences.getString("theme-id", THEME_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(
            when (themeId) {
                THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> throw IllegalArgumentException("Wrong Theme ID!!!---$themeId")
            }
        )
    }
}