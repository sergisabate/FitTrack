package com.sergisabate.fittrack.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sergisabate.fittrack.data.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    fun setDarkTheme(isDarkMode: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(isDarkMode)
        }
    }

    fun getPreferences(): StateFlow<Int> {
        return userPreferencesRepository.isInDarkModeFlow
    }
}

class ThemeViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return ThemeViewModel(userPreferencesRepository) as T
    }
}
