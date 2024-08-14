package com.sergisabate.fittrack.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sergisabate.fittrack.data.repositories.ProfileData
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    fun setProfileData(
        profileData: ProfileData,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setName(profileData.name)
                userPreferencesRepository.setAge(profileData.age)
                userPreferencesRepository.setHeight(profileData.height)
                userPreferencesRepository.setWeight(profileData.weight)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun getProfileData(): StateFlow<ProfileData> {
        val profile = ProfileData(
            name = userPreferencesRepository.nameFlow.value,
            age = userPreferencesRepository.ageFlow.value,
            weight = userPreferencesRepository.weightFlow.value,
            height = userPreferencesRepository.heightFlow.value
        )
        return MutableStateFlow(profile)
    }
}

class ProfileViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return ProfileViewModel(userPreferencesRepository) as T
    }
}
