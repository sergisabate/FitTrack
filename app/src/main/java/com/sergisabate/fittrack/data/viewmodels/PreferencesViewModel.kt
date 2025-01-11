package com.sergisabate.fittrack.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    fun setDarkTheme(isDarkMode: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(isDarkMode)
        }
    }

    fun setLocale(locale: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLocale(locale)
        }
    }

    fun setBluetoothDevice(
        bluetoothDevice: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (userPreferencesRepository.bluetoothDeviceFlow.value != bluetoothDevice) {
            viewModelScope.launch {
                try {
                    userPreferencesRepository.setBluetoothDevice(bluetoothDevice)
                    onSuccess?.invoke()
                } catch (e: Exception) {
                    onError?.invoke(e)
                }
            }
        }
    }

    fun getDarkTheme(): StateFlow<Int> {
        return userPreferencesRepository.isInDarkModeFlow
    }

    fun getLocale(): StateFlow<String> {
        return userPreferencesRepository.localeFlow
    }

    fun getBluetoothDevice(): StateFlow<String> {
        return userPreferencesRepository.bluetoothDeviceFlow
    }

    fun deleteBluetoothDevice(
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setBluetoothDevice("")
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }
}

class PreferencesViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return PreferencesViewModel(userPreferencesRepository) as T
    }
}
