package com.example.mobile_app_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_app_project.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Skeleton for Settings screen ViewModel.
 */
class SettingsViewModel(private val preferences: UserPreferences) : ViewModel() {

    val lastCityFlow: Flow<String?> = preferences.observeLastCityName()

    fun clearLastCity() {
        viewModelScope.launch {
            preferences.saveLastCityName("")
        }
    }
}
