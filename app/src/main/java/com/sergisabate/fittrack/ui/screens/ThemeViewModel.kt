package com.sergisabate.fittrack.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme

    fun getDarkTheme(): Boolean? {
        return _isDarkTheme.value
    }

    fun setDarkTheme(isDark: Boolean?) {
        viewModelScope.launch {
            _isDarkTheme.value = isDark
        }
    }
}
